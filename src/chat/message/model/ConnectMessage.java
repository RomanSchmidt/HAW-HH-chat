package chat.message.model;

import chat.Uid;

public class ConnectMessage extends AMessage {

    public ConnectMessage(Uid uidSender, Uid uidReceiver, String senderName) {
        super(new Header(MessageType.connect, uidSender, uidReceiver), null);
        this.senderName = senderName;
    }
}
