package chat.message;

import chat.Uid;
import chat.message.model.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import static com.google.gson.stream.JsonToken.END_DOCUMENT;

public abstract class Parser {
    private static final Gson _gSon = new Gson();

    public static AMessage transfer(String jsonString) {
        System.out.println("start parse: " + jsonString);

        if (!Parser.isJsonValid(jsonString)) {
            System.err.println("Not valid json: " + jsonString);
            new Error().printStackTrace();
            return null;
        }

        // @todo mach schön
        JsonObject jObject = JsonParser.parseString(jsonString).getAsJsonObject();
        if (null != jObject) {
            JsonObject header = jObject.get("header").getAsJsonObject();
            if (null != header) {
                int messageTypeSearch = header.get("messageType").getAsInt();
                MessageType messageType = MessageType.mapFromCode(messageTypeSearch);
                if (messageType != null) {
                    JsonObject uIDSenderJSON = header.getAsJsonObject().get("uidSender").getAsJsonObject();
                    Uid uidSender = new Uid(uIDSenderJSON.get("ip").getAsString(), uIDSenderJSON.get("port").getAsInt());
                    JsonObject uidReceiverJSON = header.getAsJsonObject().get("uidReceiver").getAsJsonObject();
                    Uid uidReceiver = new Uid(uidReceiverJSON.get("ip").getAsString(), uidReceiverJSON.get("port").getAsInt());
                    JsonElement contentJSONEle = jObject.get("content");
                    JsonObject contentJSON = null;
                    if (null != contentJSONEle) {
                        contentJSON = contentJSONEle.getAsJsonObject();
                    }
                    AContent content = null;
                    switch (messageType) {
                        case chatMessage:
                            if (null == contentJSON) {
                                break;
                            }
                            content = new ChatMessageContent(contentJSON.get("message").getAsString());
                            break;
                        case routingResponse:
                            if (null == contentJSON) {
                                break;
                            }
                            ArrayList<RoutingTableMessageElement> table = new ArrayList<>();
                            JsonArray routingTableJson = contentJSON.get("routingTable").getAsJsonArray();
                            for (int j = 0; j < routingTableJson.size(); ++j) {
                                JsonObject tableElementJson = routingTableJson.get(j).getAsJsonObject();
                                JsonObject destinationUidJson = tableElementJson.get("destinationUid").getAsJsonObject();
                                Uid destinationUid = new Uid(destinationUidJson.get("ip").getAsString(), destinationUidJson.get("port").getAsInt());
                                table.add(new RoutingTableMessageElement(destinationUid, tableElementJson.get("senderName").getAsString(), tableElementJson.get("costsToDestination").getAsInt()));
                            }
                            content = new RoutingMessageContent(table);
                            break;
                    }
                    String senderName = null;
                    JsonElement senderNameJson = jObject.get("senderName");
                    if (null != senderNameJson) {
                        senderName = senderNameJson.getAsString();
                    }
                    return AMessage.createByType(
                            messageType,
                            uidSender,
                            uidReceiver,
                            senderName,
                            content
                    );
                }
            }
        }
        System.out.println("parsed message: " + (jObject != null ? jObject.get("header") : "empty"));
        return null;
    }

    public static String transfer(AMessage message) {
        System.out.println("start convert: " + message.getHeader().getMessageType());
        String jsonString = Parser._gSon.toJson(message, AMessage.class);
        System.out.println("parse convert: " + jsonString);
        return jsonString;
    }

    private static boolean isJsonValid(final String json) {
        return isJsonValid(new StringReader(json));
    }

    private static boolean isJsonValid(final Reader reader) {
        return isJsonValid(new JsonReader(reader));
    }

    private static boolean isJsonValid(final JsonReader jsonReader) {
        try {
            JsonToken token;
            loop:
            while ((token = jsonReader.peek()) != END_DOCUMENT && token != null) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        break;
                    case END_ARRAY:
                        jsonReader.endArray();
                        break;
                    case BEGIN_OBJECT:
                        jsonReader.beginObject();
                        break;
                    case END_OBJECT:
                        jsonReader.endObject();
                        break;
                    case NAME:
                        jsonReader.nextName();
                        break;
                    case STRING:
                    case NUMBER:
                    case BOOLEAN:
                    case NULL:
                        jsonReader.skipValue();
                        break;
                    case END_DOCUMENT:
                        break loop;
                    default:
                        throw new AssertionError(token);
                }
            }
            return true;
        } catch (final Exception ignored) {
            return false;
        }
    }
}
