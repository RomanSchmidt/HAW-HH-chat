package chat.routing;

import chat.Uid;
import chat.client.AClient;

import java.util.HashMap;

/**
 * handle the table
 */
public class RoutingTable {
    private final HashMap<String, RoutingTableElement> _table = new HashMap<>();

    RoutingTable() {
    }

    public void addClient(AClient client, Uid gateWayUid, int metric) {
        this.removeClient(client.getUid());
        RoutingTableElement element = new RoutingTableElement(
                client.getUid(),
                client.getName(),
                gateWayUid,
                metric,
                true
        );
        this._table.put(client.getUid().toString(), element);
    }

    public void removeClient(AClient client) {
        this.removeClient(client.getUid());
    }

    public void removeClient(Uid destinationUid) {
        this._table.remove(destinationUid.toString());
    }

    /**
     * get a clone of the table
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, RoutingTableElement> getTable() {
        return (HashMap<String, RoutingTableElement>) this._table.clone();
    }

    public RoutingTableElement getRoutingElementByUid(Uid uid) {
        return this._table.get(uid.toString());
    }
}
