package chat.routing;

import chat.Uid;

public class RoutingTableElement {
    private final Uid _destinationUid;
    private final String _destinationName;
    private final Uid _nextGateWayUid;
    private int _metric = 0;
    private boolean _recentlyChanged;

    public RoutingTableElement(Uid destinationUid, String destinationName, Uid nextGateWayUid, int metric, boolean recentlyChanged) {
        this._destinationUid = destinationUid;
        this._destinationName = destinationName;
        this._nextGateWayUid = nextGateWayUid;
        this._metric = metric;
        this._recentlyChanged = recentlyChanged;
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

    public boolean getRecentlyChanged() {
        return this._recentlyChanged;
    }

    public void setRecentlyChanged(boolean recentlyChanged) {
        this._recentlyChanged = recentlyChanged;
    }

}
