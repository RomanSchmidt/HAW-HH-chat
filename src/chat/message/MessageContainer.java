package chat.message;

import chat.Server;
import chat.Uid;
import chat.client.AClient;
import chat.client.OwnClient;
import chat.message.model.AMessage;
import chat.message.model.MessageType;

/**
 * message with all information it needs
 */
public class MessageContainer {
    private final AClient _client;
    //private final Actor _from;
    //private final Actor _to;
    private AMessage _message;
    private MessageType _messageType;
    private String _jsonString;

    private MessageContainer(AClient client) {
        this._client = client;
    }

    public MessageContainer(AMessage message, AClient client) {
        this(client);
        this._message = message;
        this._messageType = MessageType.mapFromCode(message.getHeader().getMessageType());
        //this._from = from;
        //this._to = to;
        this._jsonString = Parser.transfer(message);
    }

    public MessageContainer(AMessage message) {
        this._message = message;
        this._messageType = MessageType.mapFromCode(message.getHeader().getMessageType());
        String name = this._message.getSenderName();
        this._client = new OwnClient(this._message.getHeader().getUidSender(), name);
        //this._from = from;
        //this._to = to;
        this._jsonString = Parser.transfer(message);
    }

    public MessageContainer(String jsonString) {
        this._message = Parser.transfer(jsonString);
        this._messageType = MessageType.mapFromCode(this._message.getHeader().getMessageType());
        String name = this._message.getSenderName();
        this._client = new OwnClient(this._message.getHeader().getUidSender(), name);
        //this._from = from;
        //this._to = to;
        this._jsonString = jsonString;
    }

    public static Uid getOwnUid() {
        return Server.getUid();
    }

    public AClient getClient() {
        return this._client;
    }

    public Uid getUid() {
        return this._client.getUid();
    }

    public String getJsonString() {
        return this._jsonString;
    }

    /*public Actor getFrom() {
        return this._from;
    }

    public Actor getTo() {
        return this._to;
    }*/

    public AMessage getMessage() {
        return this._message;
    }

    public MessageType getMessageType() {
        return this._messageType;
    }
}
