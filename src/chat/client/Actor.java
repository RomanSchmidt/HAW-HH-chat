package chat.client;

import chat.message.MessageContainer;

public interface Actor {
    void sendMessage(MessageContainer message);
    String getName();
}
