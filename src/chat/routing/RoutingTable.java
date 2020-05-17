package chat.routing;

import chat.Uid;
import chat.client.AClient;

import java.util.HashMap;

/**
 * handle the table
 */
public class RoutingTable {
    private final HashMap<Uid, RoutingTableElement> _table = new HashMap<>();

    RoutingTable() {
    }

    /**
     * @todo implement
     * get own instance and remove from routing table
     */
    public void disconnect(Uid uid) {
        System.err.println("implement disconnect");
    }

    /**
     * @todo implement
     */
    public void analyzeForeignTable(RoutingTable table) {
        System.err.println("implement analyzeForeignTable");
    }

    /**
     * update the table with all information it needs to be routed correctly
     */
    public void addClient(AClient client, Uid gateWayUid, int metric) {
        RoutingTableElement element = new RoutingTableElement(
                client.getUid(),
                client.getName(),
                gateWayUid,
                metric,
                true
        );
        this._table.put(client.getUid(), element);
        System.err.println("implement addClient");
    }

    /**
     * @todo implement
     * update table
     */
    public void removeClient(AClient client) {
        System.err.println("implement removeOwnClient");
    }

    /**
     * get a clone of the table
     */
    @SuppressWarnings("unchecked")
    public HashMap<Uid, RoutingTableElement> getTable() {
        return (HashMap<Uid, RoutingTableElement>) this._table.clone();
    }
}
