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
    private final HashMap<String, AClient> _clients = new HashMap<>();

    /**
     * @todo think about a map for faster remove and add
     */
    private final Server[] _knownServers = new Server[]{};
    /**
     * @todo think about a map for faster remove and add
     */
    private final AClient[] _knownClients = new ForeignClient[]{};
    private final RoutingTable _table = new RoutingTable();

    private Routing() {
    }

    public static Routing getInstance() {
        return Routing._instance;
    }

    /**
     * return a list of client names
     */
    public String[] getAllClients() {
        String[] clients = new String[this._clients.size()];

        int i = 0;
        for (String clientName : this._clients.keySet()) {
            clients[i++] = clientName;
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

    /**
     * @todo implement
     * if client is known, throw, return or return an error? @todo discuss
     * add client from known clients
     * populate new table
     */
    public void addClient(AClient client, Uid gateway, int metric) {
        this._table.addClient(client, gateway, metric);
    }

    /**
     * @todo implement
     * if client is not known, throw, return or return an error? @todo discuss
     * remove client from known clients
     */
    public void removeClient(AClient client) {
        System.err.println("implement removeClient");
        this._table.removeClient(client);
    }

    /**
     * @todo implement
     * let all known servers know about the changes, except the one you got it from
     */
    private void _populateChanges(ForeignClient from) {
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
        return this._knownClients[0];
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
