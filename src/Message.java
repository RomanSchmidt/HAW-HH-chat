/**
 * message with all information it needs
 */
public class Message {
    private final Client _from;
    private final Client _to;
    private final String _message;

    Message(Client from, Client to, String message) {
        this._from = from;
        this._to = to;
        this._message = message;
    }

    public Client getFrom() {
        return this._from;
    }

    public Client getTo() {
        return this._to;
    }

    public String getMessage() {
        return this._message;
    }
}
