package chat.communication;

import chat.Server;
import chat.Uid;
import chat.client.AClient;
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
                    elements[i.getAndIncrement()] = new RoutingTableElement(roTaMeElement.getDestinationUid(), roTaMeElement.getSenderName(), this._message.getUid(), roTaMeElement.geCostsToDestination(), false);
                });
                Routing.getInstance().addTable((OwnClient)this._message.getClient(), elements);
                break;
            case chatMessage:
                System.out.println("got chat message from: " + this._message.getMessage().getHeader().getUidSender());
                Routing.getInstance().sendMessage(this._message);
                break;
            case disconnect:
                System.out.println("got disconnect message from: " + this._message.getMessage().getHeader().getUidSender());
                Routing.getInstance().removeClient(this._message.getClient());
                break;
            case connect:
                Header header = this._message.getMessage().getHeader();
                ConnectMessage message = (ConnectMessage) this._message.getMessage();
                System.out.println("got connect message from: " + header.getUidSender() + " (" + message.getSenderName() + ")");
                OwnClient client = new OwnClient(header.getUidSender(), message.getSenderName());
                Routing.getInstance().addClient(client, Server.getUid(), 1, true);
                break;
            default:
                System.err.println("unknown message type: " + this._message.getMessageType());
                break;
        }
    }

    private MessageContainer _createRoutingMessage(Uid uidSender, Uid uidReceiver, AClient client) {
        ArrayList<RoutingTableMessageElement> elements = new ArrayList<>();
        // @todo getTable.getTable -> ist kacke
        Routing.getInstance().getTable().getTable().forEach((destinationUid, routingTableElement) -> {
            elements.add(new RoutingTableMessageElement(routingTableElement.getDestinationUid(), routingTableElement.getDestinationName(), routingTableElement.getMetric()));
        });
        RoutingMessageContent routingContent = new RoutingMessageContent(elements);
        RoutingMessage routingMessage = new RoutingMessage(routingContent, uidSender, uidReceiver);
        System.out.println("send routing table: " + uidSender + " -> " + uidReceiver);
        return new MessageContainer(routingMessage, client);
    }
}
