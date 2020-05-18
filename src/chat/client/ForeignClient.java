package chat.client;

import chat.Uid;

/**
 * handle clients and handle message send
 */
public class ForeignClient extends AClient {
    public ForeignClient(Uid uid, String name) {
        super(uid, name);
    }
}
