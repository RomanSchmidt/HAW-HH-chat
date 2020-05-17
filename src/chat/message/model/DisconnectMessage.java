package chat.message.model;

import chat.Uid;

public class DisconnectMessage extends AMessage {

    public DisconnectMessage(Uid uidSender, Uid uidReceiver) {
        super(new Header(MessageType.disconnect, uidSender, uidReceiver), null);
    }
}
