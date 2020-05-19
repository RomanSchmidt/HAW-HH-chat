package chat.message.model;

import chat.Uid;

public class ConnectMessage extends AMessage {

    public ConnectMessage(Uid uidSender, Uid uidReceiver, ConnectMessageContent content) {
        super(new Header(MessageType.connect, uidSender, uidReceiver), content);
    }
}
