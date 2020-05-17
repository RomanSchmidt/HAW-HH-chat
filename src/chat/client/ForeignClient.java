package chat.client;

import chat.message.MessageContainer;
import chat.Uid;

/**
 * handle clients and handle message send
 */
public class ForeignClient extends AClient {
    public ForeignClient(Uid uid, String name) {
        super(uid, name);
    }

    /**
     * @todo implement
     */
    @Override
    public void sendMessage(MessageContainer message) {
        System.err.println("not implemented to send something to foreign client");
    }
}
