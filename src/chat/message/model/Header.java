package chat.message.model;

import chat.Uid;

public class Header {
    private final Integer type;
    private final Uid uidSender;
    private final Uid uidReceiver;

    public Header(MessageType type, Uid uidSender, Uid uidReceiver) {
        this.type = type.getCode();
        this.uidSender = uidSender;
        this.uidReceiver = uidReceiver;
    }

    public int getType() {
        return this.type;
    }

    public Uid getUidSender() {
        return this.uidSender;
    }

    public Uid getUidReceiver() {
        return this.uidReceiver;
    }
}
