package chat.client;

import chat.Server;
import chat.Uid;
import chat.communication.Communicator;
import chat.message.MessageContainer;
import chat.message.model.ConnectMessage;

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
        Communicator.send(new MessageContainer(new ConnectMessage(uidSender, uidReceiver, Server.getName()), client));
        return client;
    }
}
