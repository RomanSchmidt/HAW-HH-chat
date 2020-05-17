package chat.client;

import chat.Server;
import chat.Uid;
import chat.message.MessageContainer;
import chat.message.MessageHandler;
import chat.message.model.ConnectMessage;
import chat.message.model.DisconnectMessage;
import chat.routing.Routing;

/**
 * internal class which acts like a client
 */
public class Client extends AClient {
    public Client(Uid uid, String name) {
        super(uid, name);
    }

    public static Client connect(Uid uidSender, Uid uidReceiver, String name) {
        System.out.println("connecting to: " + uidReceiver.toString());
        Client client = new Client(uidReceiver, name);
        MessageHandler.send(new MessageContainer(new ConnectMessage(uidSender, uidReceiver, Server.getName()), client));
        return client;
    }

    public void disconnect(Uid sender) {
        System.out.println("peer disconnecting from: " + sender);
        MessageHandler.send(new MessageContainer(new DisconnectMessage(sender, this.getUid()), this));
        Routing.getInstance().removeClient(this);
    }

    @Override
    public void sendMessage(MessageContainer message) {
        System.out.println("sending message to server: " + message.getMessage().getHeader().getMessageType());
        MessageHandler.send(message);
    }
}
