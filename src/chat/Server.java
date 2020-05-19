package chat;

import chat.cli.Cli;
import chat.client.Client;
import chat.communication.Communicator;
import chat.message.MessageContainer;
import chat.routing.Routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

/**
 * start listen on a port
 * receive messages from clients
 */
public class Server implements Runnable {
    private static Uid _uid;
    private static String _name;

    Server() {
        Server._name = Cli.getParamString("name");
        Server.getUidFromCli();
        Client client = new Client(Server._uid, Server._name);
        Routing.getInstance().addClient(client, Server.getUid(), 0, false);
    }

    public static String getName() {
        return Server._name;
    }

    public static Uid getUid() {
        return Server._uid;
    }

    public static void getUidFromCli() {
        Server._uid = new Uid(Cli.getParamString("ip"), Cli.getParamInt("port"));
    }

    /**
     * get local ip
     */
    private static String _getOwnIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("not able to resolve local ip: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static void disconnect() {
        Routing.getInstance().getClient(Server.getUid()).disconnect(false);
    }

    /**
     * start server and listen on a specific port
     */
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(Server._uid.getPort());
            while (true) {
                try (Socket clientSocket = socket.accept()) {
                    BufferedReader messageBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String jsonString = messageBuffer.lines().collect(Collectors.joining());
                    MessageContainer message = new MessageContainer(jsonString);
                    Communicator.receive(message);
                }
            }
        } catch (IOException e) {
            System.err.println("connection invalid");
            Server.getUidFromCli();
        }
    }

    public static void quit() throws InterruptedException {
        throw new InterruptedException();
    }
}
