package chat.communicator;

import chat.Server;
import chat.Uid;
import chat.client.AClient;
import chat.client.OwnClient;
import chat.message.MessageContainer;
import chat.message.MessageHandler;
import chat.message.model.*;
import chat.routing.Routing;

import java.util.ArrayList;

public class Receiver implements Runnable {

    private final MessageContainer _message;

    public Receiver(MessageContainer message) {
        this._message = message;
    }

    @Override
    public void run() {
        System.out.println("analyse message");

        switch (this._message.getMessageType()) {
            case routingResponse:
                System.err.println("implement receive routingResponse");
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
                Routing.getInstance().addClient(client, Server.getUid(), 1);
                MessageHandler.send(this._createRoutingMessage(MessageContainer.getOwnUid(), header.getUidSender(), client));
                break;
            default:
                System.err.println("unknown message type: " + this._message.getMessageType());
                break;
        }

        System.out.println("analyse message end");
    }

    private MessageContainer _createRoutingMessage(Uid uidSender, Uid uidReceiver, AClient client) {
        ArrayList<RoutingTableMessageElement> elements = new ArrayList<>();
        // @todo getTable.getTable -> ist kacke
        Routing.getInstance().getTable().getTable().forEach((destinationUid, routingTableElement) -> {
            elements.add(new RoutingTableMessageElement(destinationUid, routingTableElement.getDestinationName(), routingTableElement.getMetric()));
        });
        RoutingMessageContent routingContent = new RoutingMessageContent(elements);
        RoutingMessage routingMessage = new RoutingMessage(routingContent, uidSender, uidReceiver);
        System.out.println("send routing table: " + uidSender + " -> " + uidReceiver);
        return new MessageContainer(routingMessage, client);
    }
}
