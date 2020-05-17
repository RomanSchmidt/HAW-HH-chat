package chat.client;

import chat.Uid;
import chat.message.MessageContainer;

public class OwnClient extends AClient {
    public OwnClient(Uid uid, String name) {
        super(uid, name);
    }

    /**
     * @todo implement
     */
    @Override
    public void sendMessage(MessageContainer message) {
        System.err.println("not implemented to send something to OwnClient client");
    }
}
