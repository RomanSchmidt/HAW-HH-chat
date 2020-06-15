package chat.routing;

import chat.Uid;
import chat.cli.Cli;
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
        if (null == this._table.put(client.getUid(), element)) {
            Cli.printInfo("new user", client.getName());
        }
    }

    public RoutingTableElement removeClient(Uid destinationUid) {
        return this._table.remove(destinationUid);
    }

    /**
     * get a clone of the table
     */
    @SuppressWarnings("unchecked")
    public HashMap<Uid, RoutingTableElement> getTable() {
        return  this._table;//.clone();
    }

    public RoutingTableElement getRoutingElementByUid(Uid uid) {
        return this._table.get(uid);
    }
}
