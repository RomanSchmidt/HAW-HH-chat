package chat.message.model;

import chat.Uid;

public class ChatMessage extends AMessage {
    public ChatMessage(ChatMessageContent content, Uid uidSender, Uid uidReceiver) {
        super(new Header(MessageType.chatMessage, uidSender, uidReceiver), content);
    }
}
