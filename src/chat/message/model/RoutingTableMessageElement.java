package chat.message.model;

import chat.Uid;

public class RoutingTableMessageElement {
    private final Uid destinationUid;
    private final String userName;
    private final Integer costsToDestination;

    public RoutingTableMessageElement(Uid destinationUid, String userName, Integer costsToDestination) {
        this.destinationUid = destinationUid;
        this.userName = userName;
        this.costsToDestination = costsToDestination;
    }

    public Uid getDestinationUid() {
        return this.destinationUid;
    }

    public String getUserName() {
        return this.userName;
    }

    public Integer geCostsToDestination() {
        return this.costsToDestination;
    }
}
