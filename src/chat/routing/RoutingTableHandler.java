package chat.routing;

import chat.Uid;
import chat.client.AClient;

import java.util.HashMap;

/**
 * handle the table
 */
public class RoutingTableHandler {
    private final HashMap<Uid, RoutingTableElement> _table = new HashMap<>();

    RoutingTableHandler() {
    }

    public void addClient(AClient client, Uid gateWayUid, int metric) {
        this.removeClient(client.getUid());
        RoutingTableElement element = new RoutingTableElement(
                client.getUid(),
                client.getName(),
                gateWayUid,
                metric
        );
        this._table.put(client.getUid(), element);
    }

    public RoutingTableElement removeClient(Uid destinationUid) {
        return this._table.remove(destinationUid);
    }

    /**
     * get a clone of the table
     */
    @SuppressWarnings("unchecked")
    public HashMap<Uid, RoutingTableElement> getTable() {
        return (HashMap<Uid, RoutingTableElement>) this._table.clone();
    }

    public RoutingTableElement getRoutingElementByUid(Uid uid) {
        return this._table.get(uid);
    }
}
