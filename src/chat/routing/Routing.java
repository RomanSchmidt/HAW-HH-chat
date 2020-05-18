package chat.routing;

import chat.Server;
import chat.Uid;
import chat.cli.Cli;
import chat.client.AClient;
import chat.client.Client;
import chat.client.ForeignClient;
import chat.client.OwnClient;
import chat.communication.Communicator;
import chat.message.MessageContainer;
import chat.message.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * using the routing table to fill, analyze (call that method) and update
 */
public class Routing {
    private static final Routing _instance = new Routing();
    // client by name
    private final HashMap<String, AClient> _clientsByName = new HashMap<>();
    private final HashMap<Uid, AClient> _clientsByUId = new HashMap<>();
    private final HashMap<Uid, ArrayList<AClient>> _clientsByGateWayUid = new HashMap<>();
    private final ArrayList<Uid> _ownClientUids = new ArrayList<>();
    private final RoutingTable _table = new RoutingTable();
    private Client _connectedClient;

    private Routing() {
    }

    public static Routing getInstance() {
        return Routing._instance;
    }

    /**
     * return a list of client names
     */
    public AClient[] getAllClients() {
        AClient[] clients = new AClient[this._clientsByName.size()];

        int i = 0;
        for (AClient client : this._clientsByName.values()) {
            clients[i++] = client;
        }

        return clients;
    }

    /**
     * @todo implement
     * get a new table from other server, analyze and populate to all others, but not to the same Server you got it from
     * add foreign clients to the known clients list
     * add from to known servers if its not there
     * remove all clients from those servers that are not in the list any more
     * remove all clients that are not in the list any more
     */
    public void addTable(OwnClient from, RoutingTableElement[] elements) {
        this._analyzeTable(from, elements);
    }

    public void addClient(AClient client, Uid gateway, int metric, boolean doPopulate) {
        if (!this._clientsByUId.containsKey(client.getUid())) {
            System.out.println("do not know: " + client.getUid());
            if (client instanceof OwnClient) {
                this._ownClientUids.add(client.getUid());
            }
            if (client instanceof Client) {
                this._connectedClient = (Client) client;
            }
            System.out.println("add client: " + client.getName());
            this._table.addClient(client, gateway, metric);
            this._clientsByName.put(client.getName(), client);
            this._clientsByUId.put(client.getUid(), client);
            if (!this._clientsByGateWayUid.containsKey(gateway)) {
                this._clientsByGateWayUid.put(gateway, new ArrayList<>());
            }
            this._clientsByGateWayUid.get(gateway).add(client);
        }
        if (doPopulate) {
            this._populateChanges(this._clientsByUId.get(Server.getUid()));
        }
    }

    public void removeClient(AClient client) {
        System.err.println("removing: " + client.getName());

        System.err.println("foo: " + Server.getUid());
        this._table.removeClient(client.getUid());
        this._clientsByName.remove(client.getName());
        this._clientsByUId.remove(client.getUid());
        System.out.println("remove this._clientsByUId: " + client.getUid() + " -> " + this._clientsByUId.keySet().toString());
        this._clientsByGateWayUid.remove(client.getUid());
        this._clientsByGateWayUid.forEach((uid, clients) -> {
            clients.removeIf(clientInMap -> {
                return clientInMap.getUid().equals(client.getUid());
            });
        });

        if (client instanceof Client) {
            this._populateDisconnect((Client) client);
            System.out.println("shot down?");
            //Communicator.getExecutor().shutdown();
            //try {
            //    Communicator.getExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            //} catch (InterruptedException e) {
            //    System.out.println("down");
            //}
            //return;
        } else {
            this._populateChanges(client);
        }
    }

    private void _populateDisconnect(Client discoClient) {
        System.out.println("populating disconnect");

        this._clientsByName.forEach((name, client) -> {
            if (client instanceof ForeignClient || client.getUid().equals(Server.getUid())) {
                return;
            }
            System.out.println("send message");
            DisconnectMessage message = (DisconnectMessage) AMessage.createByType(MessageType.disconnect, discoClient.getUid(), client.getUid(), client.getName(), null);
            System.out.println("populating disconnect: " + discoClient.getName() + " -> " + client.getUid());
            Communicator.send(new MessageContainer(message, client));
        });

        if (null != this._connectedClient) {
            DisconnectMessage message = (DisconnectMessage) AMessage.createByType(MessageType.disconnect, discoClient.getUid(), this._connectedClient.getUid(), discoClient.getName(), null);
            System.out.println("populating disconnect1: " + discoClient.getName() + " -> " + this._connectedClient.getUid());
            Communicator.send(new MessageContainer(message, discoClient));
        }

        this._ownClientUids.forEach(uid -> {
            DisconnectMessage message = (DisconnectMessage) AMessage.createByType(MessageType.disconnect, discoClient.getUid(), uid, discoClient.getName(), null);
            System.out.println("populating disconnect2: " + discoClient.getName() + " -> " + uid);
            Communicator.send(new MessageContainer(message, discoClient));
        });
    }

    private void _populateChanges(AClient from) {
        System.out.println("populating table");
        ArrayList<RoutingTableMessageElement> elements = new ArrayList<>();
        this._table.getTable().forEach((uid, routingTableElement) -> {
            elements.add(new RoutingTableMessageElement(routingTableElement.getDestinationUid(), routingTableElement.getDestinationName(), routingTableElement.getMetric()));
        });
        RoutingMessageContent content = new RoutingMessageContent(elements);

        if (null != this._connectedClient && !this._connectedClient.getUid().equals(from.getUid()) && !this._connectedClient.getUid().equals(Server.getUid())) {
            AMessage message = AMessage.createByType(MessageType.routingResponse, Server.getUid(), this._connectedClient.getUid(), Server.getName(), content);
            AClient client = this._clientsByUId.get(this._connectedClient.getUid());
            System.out.println("equals? " + from.getUid() + " -- " + this._connectedClient.getUid());
            Communicator.send(new MessageContainer(message, client));
        }
        this._ownClientUids.forEach(uid -> {
                        if (!uid.equals(from.getUid())) {
                AMessage message = AMessage.createByType(MessageType.routingResponse, Server.getUid(), uid, Server.getName(), content);
                AClient client = this._clientsByUId.get(uid);
                Communicator.send(new MessageContainer(message, client));
            }
        });
    }

    private void _analyzeTable(OwnClient from, RoutingTableElement[] elements) {
        boolean hasChanges = false;
        ArrayList<Uid> handledUids = new ArrayList<>();
        //this.addClient(from, Server.getUid(), 0, false);
        for (RoutingTableElement foreignElement : elements) {
            if (foreignElement.getDestinationUid().equals(Server.getUid())) {
                continue;
            }
            if (this._ownClientUids.contains(foreignElement.getDestinationUid())) {
                continue;
            }
            handledUids.add(foreignElement.getDestinationUid());
            RoutingTableElement ownElement = this._table.getRoutingElementByUid(foreignElement.getDestinationUid());
            if (ownElement == null || foreignElement.getMetric() < ownElement.getMetric()) {
                ForeignClient client = new ForeignClient(foreignElement.getDestinationUid(), foreignElement.getDestinationName());
                this.addClient(client, foreignElement.getNextGateWayUid(), foreignElement.getMetric() + 1, false);
                hasChanges = true;
            }
        }
        this._cleanUpTable(from.getUid(), handledUids);
        if (hasChanges) {
            this._populateChanges(from);
        }
    }

    private void _cleanUpTable(Uid uid, ArrayList<Uid> handledUids) {
        var clients = this._clientsByGateWayUid.get(uid);
        if (clients == null) {
            return;
        }
        System.out.println("cleanup: " + uid);
        clients.forEach(client -> {
            if (!handledUids.contains(client.getUid())) {
                this.removeClient(client);
            }
        });
    }

    public RoutingTable getTable() {
        return this._table;
    }

    /**
     * @todo implement
     * figure out if the next receiver is the client directly or the next server for a hop.
     */
    public AClient getReceiver(Uid receiverUid) {
        RoutingTableElement element = this._table.getRoutingElementByUid(receiverUid);
        if (null == element) {
            return null;
        }
        System.out.println("got element for receiver");
        return this._clientsByUId.get(element.getNextGateWayUid());
    }

    public AClient getClient(Uid clientUidSearch) {
        System.out.println("this._clientsByUId: " + clientUidSearch + " -> " + this._clientsByUId.keySet().toString());
        return this._clientsByUId.get(clientUidSearch);
    }

    public AClient getClient(String clientNameSearch) {
        AtomicReference<AClient> foundClient = new AtomicReference<>();

        this._clientsByName.forEach((clientName, client) -> {
            if (clientName.equals(clientNameSearch)) {
                foundClient.set(client);
            }
        });
        return foundClient.get();
    }

    public void sendMessage(MessageContainer messageContainer) {
        AClient receiverClient = this.getReceiver(messageContainer.getMessage().getHeader().getUidReceiver());
        System.out.println("got receiver: " + receiverClient);
        if (null == receiverClient) {
            System.err.println("no route found!");
        } else if (receiverClient.getUid().equals(messageContainer.getUid())) {
            Cli.printChatMessage(messageContainer);
        } else {
            receiverClient.sendMessage(messageContainer);
        }
    }
}
