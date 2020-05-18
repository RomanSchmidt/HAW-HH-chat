package chat.message.model;

import chat.Uid;

public abstract class AMessage {
    private final Header header;
    private final AContent content;
    protected String senderName;

    protected AMessage(Header header, AContent content) {
        this.header = header;
        this.content = content;
    }

    public static AMessage createByType(MessageType messageType, Uid uidSender, Uid uidReceiver, String senderName, AContent content) {
        switch (messageType) {
            case connect:
                return new ConnectMessage(uidSender, uidReceiver, senderName);
            case disconnect:
                return new DisconnectMessage(uidSender, uidReceiver);
            case chatMessage:
                return new ChatMessage(content, uidSender, uidReceiver, senderName);
            case routingResponse:
                return new RoutingMessage(content, uidSender, uidReceiver);
        }
        return null;
    }

    public Header getHeader() {
        return this.header;
    }

    public AContent getContent() {
        return this.content;
    }

    public String getSenderName() {
        return this.senderName;
    }
}
