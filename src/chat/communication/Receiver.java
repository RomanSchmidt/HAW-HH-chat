package chat.communication;

import chat.Server;
import chat.client.OwnClient;
import chat.message.MessageContainer;
import chat.message.model.*;
import chat.routing.Routing;
import chat.routing.RoutingTableElement;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Receiver implements Runnable {

    private final MessageContainer _message;

    public Receiver(MessageContainer message) {
        this._message = message;
    }

    @Override
    public void run() {
        switch (this._message.getMessageType()) {
            case routingResponse:
                System.out.println("got routing from: " + this._message.getMessage().getHeader().getUidSender());
                ArrayList<RoutingTableMessageElement> messageElements = ((RoutingMessageContent) this._message.getMessage().getContent()).getRoutingTable();
                RoutingTableElement[] elements = new RoutingTableElement[messageElements.size()];
                AtomicInteger i = new AtomicInteger();
                messageElements.forEach(roTaMeElement -> {
                    elements[i.getAndIncrement()] = new RoutingTableElement(roTaMeElement.getDestinationUid(), roTaMeElement.getUserName(), this._message.getUid(), roTaMeElement.geCostsToDestination());
                });
                Routing.getInstance().addTable(this._message.getMessage().getHeader().getUidSender(), elements);
                break;
            case chatMessage:
                System.out.println("got chat message from: " + this._message.getMessage().getHeader().getUidSender());
                Routing.getInstance().sendMessage(this._message);
                break;
            case disconnect:
                System.out.println("got disconnect message from: " + this._message.getMessage().getHeader().getUidSender());
                Routing.getInstance().removeClient(this._message.getClient(), true);
                break;
            case connect:
                Header header = this._message.getMessage().getHeader();
                ConnectMessage message = (ConnectMessage) this._message.getMessage();
                ConnectMessageContent content = (ConnectMessageContent) message.getContent();
                String name = "";
                if(null != content) {
                    name = content.getUserName();
                }
                System.out.println("got connect message from: " + header.getUidSender() + " (" + name + ")");
                OwnClient client = new OwnClient(header.getUidSender(), name);
                Routing.getInstance().addClient(client, Server.getUid(), 1, true);
                break;
            default:
                System.err.println("unknown message type: " + this._message.getMessageType());
                break;
        }
    }
}
