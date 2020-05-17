package chat.message.model;

import chat.Uid;

public class Header {
    private final Integer messageType;
    private final Uid uidSender;
    private final Uid uidReceiver;

    public Header(MessageType type, Uid uidSender, Uid uidReceiver) {
        this.messageType = type.getCode();
        this.uidSender = uidSender;
        this.uidReceiver = uidReceiver;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public Uid getUidSender() {
        return this.uidSender;
    }

    public Uid getUidReceiver() {
        return this.uidReceiver;
    }
}
