package chat.routing;

import chat.Server;
import chat.Uid;
import chat.client.AClient;
import chat.client.ForeignClient;
import chat.message.MessageContainer;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * using the routing table to fill, analyze (call that method) and update
 */
public class Routing {
    private static final Routing _instance = new Routing();
    // client by name
    private final HashMap<String, AClient> _clients = new HashMap<>();

    private final RoutingTable _table = new RoutingTable();

    private Routing() {
    }

    public static Routing getInstance() {
        return Routing._instance;
    }

    /**
     * return a list of client names
     */
    public AClient[] getAllClients() {
        AClient[] clients = new AClient[this._clients.size()];

        int i = 0;
        for (AClient client : this._clients.values()) {
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
    public void addTable(ForeignClient from, RoutingTable table) {
        boolean hasChanges = this._analyzeTable(table);
        if (hasChanges) {
            this._populateChanges(from);
        }
    }

    public void addClient(AClient client, Uid gateway, int metric) {
        System.out.println("add client: " + client.getName());
        this._table.addClient(client, gateway, metric);
        this._clients.put(client.getName(), client);
        this._populateChanges(this._clients.get(Server.getUid()));
    }

    public void removeClient(AClient client) {
        System.err.println("implement removeClient");
        this._table.removeClient(client);
        this._clients.remove(client.getName());
        this._populateChanges(this._clients.get(Server.getUid()));
    }

    /**
     * @todo implement
     * let all known servers know about the changes, except the one you got it from
     */
    private void _populateChanges(AClient from) {
        System.err.println("implement _populateChanges");
    }

    /**
     * @todo implement
     * analyze table, return if there are chages or not
     */
    private boolean _analyzeTable(RoutingTable table) {
        System.err.println("implement _analyzeTable");
        return false;
    }

    public RoutingTable getTable() {
        return this._table;
    }

    /**
     * @todo implement
     * figure out if the next receiver is the client directly or the next server for a hop.
     */
    public AClient getReceiver(Uid receiverUid) {
        System.err.println("implement getReceiver");
        return null;
    }

    public AClient getClientByName(String clientNameSearch) {
        AtomicReference<AClient> foundClient = null;
        this._clients.forEach((clientName, client) -> {
            if (clientName.equals(clientNameSearch)) {
                foundClient.set(client);
            }
        });
        return foundClient.get();
    }

    public void sendMessage(MessageContainer messageContainer) {
        AClient receiverClient = this.getReceiver(messageContainer.getMessage().getHeader().getUidReceiver());
        receiverClient.sendMessage(messageContainer);
    }
}
