package chat.message.model;

import chat.Uid;

public class RoutingTableMessageElement {
    private final Uid destinationUid;
    private final String senderName;
    private final Integer costsToDestination;

    public RoutingTableMessageElement(Uid destinationUid, String senderName, Integer costsToDestination) {
        this.destinationUid = destinationUid;
        this.senderName = senderName;
        this.costsToDestination = costsToDestination;
    }

    public Uid getDestinationUid() {
        return this.destinationUid;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public Integer geCostsToDestination() {
        return this.costsToDestination;
    }
}
