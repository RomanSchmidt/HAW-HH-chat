package chat.message.model;

import java.util.ArrayList;

public class RoutingMessageContent extends AContent {
    private final ArrayList<RoutingTableMessageElement> routingTable;

    public RoutingMessageContent(ArrayList<RoutingTableMessageElement> elements) {
        super(null);
        this.routingTable = elements;
    }

    public ArrayList<RoutingTableMessageElement> getRoutingTable() {
        return this.routingTable;
    }
}
