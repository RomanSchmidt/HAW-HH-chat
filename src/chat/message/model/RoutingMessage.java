package chat.message.model;

import chat.Uid;

public class RoutingMessage extends AMessage {
    public RoutingMessage(AContent content, Uid uidSender, Uid uidReceiver) {
        super(new Header(MessageType.routingResponse, uidSender, uidReceiver), content);
    }
}
