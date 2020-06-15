package chat.cli;

import chat.Server;
import chat.Uid;
import chat.client.AClient;
import chat.client.Client;
import chat.message.MessageContainer;
import chat.message.model.AMessage;
import chat.message.model.ChatMessage;
import chat.message.model.ChatMessageContent;
import chat.message.model.MessageType;
import chat.routing.Routing;

import java.util.Scanner;

/**
 * commands for all commands in the CLI
 */
public abstract class Cli {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static Client _client;
    private static AClient _clientToChatWith;
    private static boolean _debug = false;

    public Cli() {

    }

    public static void clientLeft(AClient client, String name) {
        if (Cli._clientToChatWith != null && Cli._clientToChatWith.getUid().equals(client.getUid())) {
            Cli._clientToChatWith = null;
        }
        Cli.printNote("removing", client.getUid() + Cli.ANSI_BLUE + " (" + name + ")" + Cli.ANSI_RESET);
    }

    /**
     * run, map the string to commands, execute commands, quit if the exit command is send
     */
    public static void getCommands() {
        boolean doRun = true;
        Cli._printAllCommands();
        while (doRun) {
            String command = Cli._readNextCommand();
            if (command.startsWith("/")) {
                CliCommand convertedCommand = Cli._convertCommand(command);
                if (convertedCommand == null) {
                    Cli.printError("unknown command", command);
                } else {
                    doRun = Cli._executeCommand(convertedCommand);
                }
            } else {
                Cli._sendChatMessage(command);
            }
        }
    }

    /**
     * make an output of all commands
     */
    private static void _printAllCommands() {
        System.out.println("___________COMMANDS___________");
        for (CliCommand command : CliCommand.values()) {
            System.out.println(Cli.ANSI_BLUE + command.getName() + Cli.ANSI_RESET + " (" + command.getDescription() + ")");
        }
        System.out.println("______________________________");
    }

    // switch case with command executes and parameter check -> add to blocking queue
    private static boolean _executeCommand(CliCommand command) {
        switch (command) {
            case Help:
                Cli._printAllCommands();
                return true;
            case Select:
                Cli._chooseClient();
                return true;
            case SendAll:
                Cli._clientToChatWith = null;
                return true;
            case Debug:
                Cli._toggleDebug();
                return true;
            case UserList:
                Cli._printAllClients();
                return true;
            case Connect:
                String ip = Cli.getParamString("ip", Server.getLocalHostAddress());
                Integer port = Cli.getParamInt("port", 8081);
                Cli._connect(new Uid(ip, port));
                return true;
            case Exit:
                Cli.printSuccess("exiting", Server.getUid().toString());
                Server.disconnect();
                return false;
            case Routing:
                Cli._printTable();
                return true;
        }
        Cli.printError("can not find execution for command", command.getName());
        return false;
    }

    private static void _toggleDebug() {
        Cli._debug = !Cli._debug;
        Cli.printInfo("Debug is " + (Cli._debug ? "on" : "off"));
    }

    public static void printInfo(String notification) {
        Cli.printInfo(notification, null);
    }

    public static void printInfo(String notification, String value) {
        Cli._print(notification, value, Cli.ANSI_CYAN);
    }

    public static void printError(String notification) {
        Cli.printError(notification, null);
    }

    public static void printError(String notification, String value) {
        Cli._print(notification, value, Cli.ANSI_RED);
    }

    public static void printNote(String notification) {
        Cli.printNote(notification, null);
    }

    public static void printNote(String notification, String value) {
        Cli._print(notification, value, Cli.ANSI_BLUE);
    }

    public static void printSuccess(String notification) {
        Cli.printSuccess(notification, null);
    }

    public static void printSuccess(String notification, String value) {
        Cli._print(notification, value, Cli.ANSI_GREEN);
    }

    public static void printDebug(String notification) {
        Cli.printDebug(notification, null);
    }

    public static void printDebug(String notification, String value) {
        if (Cli._debug) {
            Cli._print(notification, value, Cli.ANSI_YELLOW);
        }
    }

    private static void _print(String notification, String value, String color) {
        System.out.println(color + notification + Cli.ANSI_RESET + (value == null ? "" : ": " + value));
    }

    private static void _sendToAll(String chatMessage) {
        Cli.printInfo("(to all)", chatMessage);
        for (AClient client : Routing.getInstance().getAllClients()) {
            if (!(client instanceof Client)) {
                ChatMessageContent content = new ChatMessageContent(chatMessage);
                Cli.printDebug("chat(" + content.getUserName() + ")", chatMessage);
                ChatMessage message = (ChatMessage) AMessage.createByType(MessageType.chatMessage, Server.getUid(), client.getUid(), content);
                MessageContainer container = new MessageContainer(message, client);
                client.sendMessage(container);
            }
        }
    }

    private static void _printTable() {
        System.out.println("_____________TABLE_____________");
        Routing.getInstance().getTable().forEach((destinationUid, routingTableElement) -> {
            System.out.println(
                    "DestinationUid: " +
                            routingTableElement.getDestinationUid() +
                            "; DestinationName: " +
                            routingTableElement.getDestinationName() +
                            "; GateWayUid: " +
                            routingTableElement.getNextGateWayUid() +
                            "; Metric: " +
                            routingTableElement.getMetric()
            );
        });
        System.out.println("_______________________________");
    }

    private static void _chooseClient() {
        Cli._clientToChatWith = null;
        String clientName = Cli.getParamString("client name", "roman2");
        AClient client = Routing.getInstance().getClient(clientName);
        if (null == client) {
            Cli.printError("unknown User");
        } else {
            if (clientName.equals(Server.getName())) {
                Cli.printError("it's not healthy to talk to your self");
                return;
            }
            Cli._clientToChatWith = client;
            Cli.printSuccess("chosen user", client.getName());
        }
    }

    private static void _printAllClients() {
        System.out.println("_____________USER_____________");
        for (AClient client : Routing.getInstance().getAllClients()) {
            if (client instanceof Client) {
                System.out.println(Cli.ANSI_BLUE + client.getName() + Cli.ANSI_RESET);
            } else {
                System.out.println(client.getName());
            }
        }
        System.out.println("______________________________");
    }

    private static void _sendChatMessage(String chatMessage) {
        if (null == Cli._clientToChatWith) {
            Cli._sendToAll(chatMessage);
        } else {
            ChatMessageContent content = new ChatMessageContent(chatMessage);
            ChatMessage message = (ChatMessage) AMessage.createByType(MessageType.chatMessage, Server.getUid(), Cli._clientToChatWith.getUid(), content);
            Cli.printNote("(" + content.getUserName() + ")", chatMessage);
            MessageContainer container = new MessageContainer(message, Cli._clientToChatWith);
            Cli._clientToChatWith.sendMessage(container);
        }
    }

    private static void _connect(Uid uid) {
        if (null != Cli._client && !Cli._client.getUid().equals(uid)) {
            Cli._client.disconnect(false, false);
        }
        Cli._client = Client.connect(Server.getUid(), uid, Server.getName());
    }

    public static String getParamString(String name, String defaultValue) {
        Cli._printGetParam(name, defaultValue);
        Scanner in = new Scanner(System.in);

        return _toggleInput(in.next(), defaultValue);
    }

    public static Integer getParamInt(String name, int defaultValue) {
        Cli._printGetParam(name, defaultValue);
        Scanner in = new Scanner(System.in);

        return _toggleInput(in.nextInt(), defaultValue);
    }

    private static <T> T _toggleInput(T value, T defaultValue) {
        if (value.toString().equals("0")) {
            return defaultValue;
        }
        return value;
    }

    private static <T> void _printGetParam(String name, T defaultValue) {
        System.out.print(name + " (0 for \"" + defaultValue + "\"): ");
    }

    // convert the string to known commands from CliCommand or return null
    // make sure the parameters are set if needed (send -> who to -> which message...)
    private static CliCommand _convertCommand(String commandString) {
        for (CliCommand command : CliCommand.values()) {
            if (command.getName().equals(commandString)) {
                return command;
            }
        }
        return null;
    }

    // get command and parameters from system input
    private static String _readNextCommand() {
        Cli.printInfo("waiting for commands");
        return new Scanner(System.in).nextLine();
    }

    public static void printChatMessage(MessageContainer messageContainer) {
        ChatMessage message = (ChatMessage) messageContainer.getMessage();
        ChatMessageContent content = (ChatMessageContent) message.getContent();
        String name = Routing.getInstance().getNameOfUid(message.getHeader().getUidSender());
        Cli.printInfo("(" + name + ")", content.getMessage());
    }

    public static void shutDown() {
        Cli.printInfo("tschööö");
        try {
            Server.quit();
        } catch (InterruptedException ignored) {
        }
        System.exit(1);
    }
}
