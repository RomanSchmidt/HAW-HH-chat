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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * using the routing table to fill, analyze (call that method) and update
 */
public class Routing {
    private static final Routing _instance = new Routing();
    private final HashMap<String, AClient> _clientsByName = new HashMap<>();
    private final HashMap<Uid, AClient> _clientsByUId = new HashMap<>();
    private final HashMap<Uid, ArrayList<AClient>> _clientsByGateWayUid = new HashMap<>();
    private final ArrayList<Uid> _ownClientUids = new ArrayList<>();
    private final RoutingTableHandler _tableHandler = new RoutingTableHandler();

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

    public void addTable(Uid fromUid, RoutingTableElement[] elements) {
        boolean hasChanges = false;
        ArrayList<Uid> handledUids = new ArrayList<>();
        for (RoutingTableElement foreignElement : elements) {
            if (foreignElement.getDestinationUid().equals(Server.getUid())) {
                continue;
            }
            if (this._ownClientUids.contains(foreignElement.getDestinationUid())) {
                continue;
            }
            handledUids.add(foreignElement.getDestinationUid());
            RoutingTableElement ownElement = this._tableHandler.getRoutingElementByUid(foreignElement.getDestinationUid());
            if (ownElement == null || foreignElement.getMetric() < ownElement.getMetric()) {
                AClient client;
                if (foreignElement.getDestinationUid().equals(fromUid)) {
                    client = new OwnClient(foreignElement.getDestinationUid(), foreignElement.getDestinationName());
                } else {
                    client = new ForeignClient(foreignElement.getDestinationUid(), foreignElement.getDestinationName());
                }
                this.addClient(client, foreignElement.getNextGateWayUid(), foreignElement.getMetric() + 1, false);
                hasChanges = true;
            }
        }
        this._cleanUpTable(fromUid, handledUids);
        if (hasChanges) {
            this._populateChanges(fromUid);
        }
    }

    public void addClient(AClient client, Uid gateway, int metric, boolean doPopulate) {
        if (!this._clientsByUId.containsKey(client.getUid())) {
            if (client instanceof OwnClient) {
                this._ownClientUids.add(client.getUid());
            }
            this._tableHandler.addClient(client, gateway, metric);
            this._clientsByName.put(client.getName(), client);
            this._clientsByUId.put(client.getUid(), client);
            if (!client.getUid().equals(gateway)) {
                if (!this._clientsByGateWayUid.containsKey(gateway)) {
                    this._clientsByGateWayUid.put(gateway, new ArrayList<>());
                }
                this._clientsByGateWayUid.get(gateway).add(client);
            }
        }
        if (doPopulate) {
            this._populateChanges(Server.getUid());
        }
    }

    public void removeClient(AClient client, boolean populate) {
        RoutingTableElement element = this._tableHandler.removeClient(client.getUid());
        if (null == element || null == element.getDestinationName()) {
            return;
        }
        Cli.clientLeft(client, element.getDestinationName());
        this._clientsByName.remove(element.getDestinationName());
        this._clientsByUId.remove(client.getUid());
        this._clientsByGateWayUid.remove(client.getUid());
        this._clientsByGateWayUid.forEach((uid, clients) -> {
            clients.removeIf(clientInMap -> {
                return clientInMap.getUid().equals(client.getUid());
            });
        });

        if (client instanceof Client) {
            this._populateDisconnect((Client) client);
            System.out.println("shoot down?");
            Communicator.getExecutor().shutdown();
            try {
                if (!Communicator.getExecutor().awaitTermination(10, TimeUnit.SECONDS)) {
                    Communicator.getExecutor().shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("down");
            } finally {
                Cli.shutDown();
            }
        } else if (populate) {
            this._populateChanges(client.getUid());
        }
    }

    private void _populateDisconnect(Client discoClient) {
        System.out.println("populating disconnect");

        this._ownClientUids.forEach(uid -> {
            if (uid.equals(Server.getUid())) {
                return;
            }
            AClient client = this._clientsByUId.get(uid);
            if (client == null) {
                return;
            }
            DisconnectMessage message = (DisconnectMessage) AMessage.createByType(MessageType.disconnect, discoClient.getUid(), uid, discoClient.getName(), null);
            Communicator.send(new MessageContainer(message, client));
        });
    }

    private void _populateChanges(Uid fromUid) {
        System.out.println("populating table");
        ArrayList<RoutingTableMessageElement> elements = new ArrayList<>();
        this._tableHandler.getTable().forEach((uid, routingTableElement) -> {
            elements.add(new RoutingTableMessageElement(routingTableElement.getDestinationUid(), routingTableElement.getDestinationName(), routingTableElement.getMetric()));
        });
        RoutingMessageContent content = new RoutingMessageContent(elements);

        this._ownClientUids.forEach(uid -> {
            if (!uid.equals(fromUid)) {
                AMessage message = AMessage.createByType(MessageType.routingResponse, Server.getUid(), uid, Server.getName(), content);
                AClient client = this._clientsByUId.get(uid);
                Communicator.send(new MessageContainer(message, client));
            }
        });
    }

    private void _cleanUpTable(Uid uid, ArrayList<Uid> handledUids) {
        var clients = this._clientsByGateWayUid.get(uid);
        if (clients == null) {
            return;
        }
        System.out.println("cleanup: " + uid);
        System.out.println(handledUids.toString());
        ArrayList<AClient> clientsToDelete = new ArrayList<>();
        clients.forEach(client -> {
            if (!handledUids.contains(client.getUid())) {
                clientsToDelete.add(client);
            }
        });
        clientsToDelete.forEach(client -> {
            this.removeClient(client, false);
        });
    }

    public HashMap<Uid, RoutingTableElement> getTable() {
        return this._tableHandler.getTable();
    }

    public AClient getReceiver(Uid receiverUid) {
        RoutingTableElement element = this._tableHandler.getRoutingElementByUid(receiverUid);
        if (null == element) {
            return null;
        }
        System.out.println("got element for receiver");
        return this._clientsByUId.get(element.getNextGateWayUid());
    }

    public AClient getClient(Uid clientUidSearch) {
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
        if (null == receiverClient) {
            System.err.println("no route found!");
            return;
        }
        System.out.println("got receiver: " + receiverClient.getUid());
        if (receiverClient.getUid().equals(Server.getUid())) {
            Cli.printChatMessage(messageContainer);
        } else {
            receiverClient.sendMessage(messageContainer);
        }
    }
}
