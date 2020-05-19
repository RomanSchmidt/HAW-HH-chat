package chat.message.model;

public class ChatMessageContent extends AContent {
    private final String message;

    public ChatMessageContent(String message, String userName) {
        super(userName);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
