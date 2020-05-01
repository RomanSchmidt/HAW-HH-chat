/**
 * using the routing table to fill, analyze (call that method) and update
 */
public class Routing {
    public final String name;

    /**
     * @todo think about a map for faster remove and add
     */
    private final Server[] _knownServers = new Server[]{};
    /**
     * @todo think about a map for faster remove and add
     */
    private final Client[] _knownClients = new Client[]{};
    private final RoutingTable _table = new RoutingTable();

    Routing(String name) {
        this.name = name;
    }

    /**
     * @todo implement
     * get a new table from other server, analyze and populate to all others, but not to the same Server you got it from
     * add foreign clients to the known clients list
     * add from to known servers if its not there
     * remove all clients from those servers that are not in the list any more
     * remove all clients that are not in the list any more
     */
    public void addTable(Client from, RoutingTable table) {
        boolean hasChanges = this._analyzeTable(table);
        if (hasChanges) {
            this._populateChanges(from);
        }
    }

    /**
     * @todo implement
     * if client is known, throw, return or return an error? @todo discuss
     * add client from known clients
     */
    public void addOwnClient(Client client) {
        this._table.addOwnClient(client);
    }

    /**
     * @todo implement
     * if client is not known, throw, return or return an error? @todo discuss
     * remove client from known clients
     */
    public void removeOwnClient(Client client) {
        this._table.removeOwnClient(client);
    }

    /**
     * @todo implement
     * let all known servers know about the changes, except the one you got it from
     */
    private void _populateChanges(Client from) {
    }

    /**
     * @todo implement
     * analyze table, return if there are chages or not
     */
    private boolean _analyzeTable(RoutingTable table) {
        return false;
    }

    public RoutingTable getTable() {
        return this._table;
    }

    /**
     * @todo implement
     * figure out if the next receiver is the client directly or the next server for a hop.
     */
    public Receiver getReceiver() {
        return this._knownClients[0];
    }
}
