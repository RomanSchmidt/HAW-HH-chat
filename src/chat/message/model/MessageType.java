package chat.message.model;

public enum MessageType {
    chatMessage(1),
    routingResponse(2),
    connect(3),
    disconnect(4);

    private final int name;

    MessageType(int name) {
        this.name = name;
    }

    public static MessageType mapFromCode(int messageTypeSearch) {
        for (int i = 0; i < MessageType.values().length; ++i) {
            MessageType messageType = MessageType.values()[i];
            if (messageType.getCode() == messageTypeSearch) {
                return messageType;
            }
        }
        return null;
    }

    public int getName() {
        return this.name;
    }

    public int getCode() {
        return this.name;
    }
}
