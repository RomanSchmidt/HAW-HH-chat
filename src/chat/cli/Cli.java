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
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static Client _client;
    private static AClient _clientToChatWith;

    public Cli() {

    }

    public static void clientLeft(AClient client, String name) {
        if (Cli._clientToChatWith != null && Cli._clientToChatWith.getUid().equals(client.getUid())) {
            Cli._clientToChatWith = null;
        }
        System.out.println(ANSI_RED + "removing: " + ANSI_RESET + client.getUid() + ANSI_BLUE + " (" + name + ")" + ANSI_RESET);
    }

    /**
     * run, map the string to commands, execute commands, quit if the exit command is send
     */
    public static void getCommands() throws InterruptedException {
        boolean doRun = true;
        Cli._printAllCommands();
        while (doRun) {
            String command = Cli._readNextCommand();
            if (command.startsWith("/")) {
                CliCommand convertedCommand = Cli._convertCommand(command);
                if (convertedCommand == null) {
                    System.out.print(ANSI_RED + "unknown command: " + ANSI_RESET + command + "\n");
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
            System.out.println(ANSI_BLUE + command.getName() + ANSI_RESET + " (" + command.getDescription() + ")");
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
            case UserList:
                Cli._printAllClients();
                return true;
            case Connect:
                String ip = Cli.getParamString("ip");
                Integer port = Cli.getParamInt("port");
                Cli._connect(new Uid(ip, port));
                return true;
            case Exit:
                System.out.println("exiting: " + Server.getUid());
                Server.disconnect();
                return false;
            case Routing:
                Cli._printTable();
                return true;
        }
        System.err.println("can not find execution for commandContainer: " + command.getName());
        return false;
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
        String clientName = Cli.getParamString("client name");
        AClient client = Routing.getInstance().getClient(clientName);
        if (null == client) {
            System.out.println(ANSI_RED + "unknown User" + ANSI_RESET);
        } else {
            if (clientName.equals(Server.getName())) {
                System.out.println(ANSI_RED + "it's not healthy to talk to your self" + ANSI_RESET);
                return;
            }
            Cli._clientToChatWith = client;
            System.out.println(ANSI_GREEN + "chosen user: " + ANSI_RESET + client.getName());
        }
    }

    private static void _printAllClients() {
        System.out.println("_____________USER_____________");
        for (AClient client : Routing.getInstance().getAllClients()) {
            if (client instanceof Client) {
                System.out.println(ANSI_BLUE + client.getName() + ANSI_RESET);
            } else {
                System.out.println(client.getName());
            }
        }
        System.out.println("______________________________");
    }

    private static void _sendChatMessage(String chatMessage) {
        if (null == Cli._clientToChatWith) {
            System.out.println(ANSI_RED + "chose a user before." + ANSI_RESET);
            Cli._printAllCommands();
        } else {
            ChatMessageContent content = new ChatMessageContent(chatMessage, Server.getName());
            ChatMessage message = (ChatMessage) AMessage.createByType(MessageType.chatMessage, Server.getUid(), Cli._clientToChatWith.getUid(), content);
            System.out.println("chat to: " + content.getUserName());
            MessageContainer container = new MessageContainer(message, Cli._clientToChatWith);
            Cli._clientToChatWith.sendMessage(container);
        }
    }

    private static void _connect(Uid uid) {
        if (null != Cli._client) {
            Cli._client.disconnect(false);
        }
        Cli._client = Client.connect(Server.getUid(), uid, Server.getName());
    }

    public static String getParamString(String name) {
        System.out.print(name + ": ");
        Scanner in = new Scanner(System.in);

        return in.next();
    }

    public static Integer getParamInt(String name) {
        System.out.print(name + ": ");
        Scanner in = new Scanner(System.in);

        return in.nextInt();
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
        System.out.print("enter command: ");
        return new Scanner(System.in).nextLine();
    }

    public static void printChatMessage(MessageContainer messageContainer) {
        ChatMessage message = (ChatMessage) messageContainer.getMessage();
        ChatMessageContent content = (ChatMessageContent) message.getContent();
        System.out.println(ANSI_CYAN + "(" + content.getUserName() + ")" + ANSI_RESET + " " + content.getMessage());
    }

    public static void shutDown() {
        System.out.println("tschööö");
        try {
            Server.quit();
        } catch (InterruptedException ignored) {
        }
        System.exit(1);
    }
}
