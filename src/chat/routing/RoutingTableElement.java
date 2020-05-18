package chat.routing;

import chat.Uid;

public class RoutingTableElement {
    private final Uid _destinationUid;
    private final String _destinationName;
    private final Uid _nextGateWayUid;
    private int _metric = 0;

    public RoutingTableElement(Uid destinationUid, String destinationName, Uid nextGateWayUid, int metric) {
        this._destinationUid = destinationUid;
        this._destinationName = destinationName;
        this._nextGateWayUid = nextGateWayUid;
        this._metric = metric;
    }

    public String getDestinationName() {
        return this._destinationName;
    }

    public Uid getDestinationUid() {
        return this._destinationUid;
    }

    public Uid getNextGateWayUid() {
        return this._nextGateWayUid;
    }

    public int getMetric() {
        return this._metric;
    }
}
