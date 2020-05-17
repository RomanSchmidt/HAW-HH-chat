package chat.client;

import chat.Server;
import chat.Uid;
import chat.message.MessageContainer;
import chat.message.MessageHandler;
import chat.message.model.ConnectMessage;
import chat.message.model.DisconnectMessage;
import chat.routing.Routing;

public class Client extends AClient {
    public Client(Uid uid, String name) {
        super(uid, name);
    }

    public static Client connect(Uid uidSender, Uid uidReceiver, String name) {
        System.out.println("connecting to: " + uidReceiver.toString() + "(" + name + ")");
        Client client = new Client(uidReceiver, name);
        MessageHandler.send(new MessageContainer(new ConnectMessage(uidSender, uidReceiver, name), client));
        Routing.getInstance().addClient(client, Server.getUid(), 1);
        return client;
    }

    // @todo implement
    // remove from table
    public void disconnect(Uid sender) {
        System.err.println("implement client disco");
        MessageHandler.send(new MessageContainer(new DisconnectMessage(sender, this.getUid()), this));
        System.out.println("Socket closed...");
    }

    @Override
    public void sendMessage(MessageContainer message) {
        System.out.println("sending message to server: " + message.getMessage().getHeader().getMessageType());
        MessageHandler.send(message);
    }
}
