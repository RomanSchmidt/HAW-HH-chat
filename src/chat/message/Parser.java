package chat.message;

import chat.Uid;
import chat.cli.Cli;
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
        if (!Parser.isJsonValid(jsonString)) {
            Cli.printError("Not valid json", jsonString);
            return null;
        }

        JsonObject jObject = JsonParser.parseString(jsonString).getAsJsonObject();
        if (null != jObject) {
            JsonObject header = jObject.get("header").getAsJsonObject();
            if (null != header) {
                int messageTypeSearch = header.get("type").getAsInt();
                MessageType messageType = MessageType.mapFromCode(messageTypeSearch);
                if (messageType != null) {
                    JsonElement contentJSONEle = jObject.get("content");
                    JsonObject contentJSON = null;
                    if (null != contentJSONEle) {
                        contentJSON = contentJSONEle.getAsJsonObject();
                    }
                    String userName = Parser._getUserName(contentJSON);

                    return AMessage.createByType(
                            messageType,
                            Parser._getUid(header.getAsJsonObject().get("uidSender").getAsJsonObject()),
                            Parser._getUid(header.getAsJsonObject().get("uidReceiver").getAsJsonObject()),
                            Parser._getContent(messageType, contentJSON, userName)
                    );
                }
            }
        }
        Cli.printDebug("parsed message", jObject != null ? jObject.get("header").toString() : "empty");
        return null;
    }

    private static Uid _getUid(JsonObject uidSender) {
        return new Uid(uidSender.get("ip").getAsString(), uidSender.get("port").getAsInt());
    }

    private static String _getUserName(JsonObject contentJSON) {
        String userName = null;
        if (null != contentJSON) {
            JsonElement userNameJson = contentJSON.get("userName");
            if (null != userNameJson) {
                userName = userNameJson.getAsString();
            }
        }
        return userName;
    }

    private static AContent _getContent(MessageType messageType, JsonObject contentJSON, String userName) {
        AContent content = null;
        switch (messageType) {
            case connect:
                if (null == contentJSON) {
                    break;
                }
                content = new ConnectMessageContent(userName);
                break;
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
                    table.add(new RoutingTableMessageElement(destinationUid, tableElementJson.get("userName").getAsString(), tableElementJson.get("costsToDestination").getAsInt()));
                }
                content = new RoutingMessageContent(table);
                break;
        }
        return content;
    }

    public static String transfer(AMessage message) {
        return Parser._gSon.toJson(message, AMessage.class);
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
