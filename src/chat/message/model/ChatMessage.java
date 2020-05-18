package chat.message.model;

import chat.Uid;

public class ChatMessage extends AMessage {
    public ChatMessage(AContent content, Uid uidSender, Uid uidReceiver, String senderName) {
        super(new Header(MessageType.chatMessage, uidSender, uidReceiver), content);
        this.senderName = senderName;
    }
}
