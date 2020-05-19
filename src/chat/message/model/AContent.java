package chat.message.model;

public abstract class AContent {
    private final String userName;

    AContent(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
