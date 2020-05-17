package chat.message;

import chat.Server;
import chat.Uid;
import chat.client.AClient;
import chat.client.Client;
import chat.message.model.MessageType;
import chat.message.model.AMessage;

/**
 * message with all information it needs
 */
public class MessageContainer {
    //private final Actor _from;
    //private final Actor _to;
    private  AMessage _message;
    private  MessageType _messageType;
    private final AClient _client;
    private  String _jsonString;

    public AClient getClient() {
        return this._client;
    }

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

    public MessageContainer(String jsonString, Client client) {
        this(client);
        this._message = Parser.transfer(jsonString);
        this._messageType = MessageType.mapFromCode(this._message.getHeader().getMessageType());
        //this._from = from;
        //this._to = to;
        this._jsonString = jsonString;
    }

    public static Uid getOwnUid() {
        return Server.getUid();
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
