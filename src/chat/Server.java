package chat;

import chat.client.Client;
import chat.message.MessageContainer;
import chat.message.MessageHandler;
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
 * send a message to a known client
 */
public class Server implements Runnable {
    private static Uid _uID;
    private static String _name;

    Server(Integer port, String name) {
        Server._uID = new Uid(this._getOwnIp(), port);
        Server._name = name;
    }

    public static String getName() {
        return Server._name;
    }

    public static Uid getUid() {
        return Server._uID;
    }

    /**
     * get local ip
     */
    private String _getOwnIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
            // could be used for name
            System.out.println("Host Name:- " + inetAddress.getHostName());
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("not able to resolve local ip: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * start server and listen on a specific port
     */
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(this._uID.getPort());
            while (true) {
                System.out.println("server waits for client");
                try (Socket clientSocket = socket.accept()) {
                    System.out.println("client accept: " + clientSocket.getRemoteSocketAddress());
                    BufferedReader messageBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String jsonString = messageBuffer.lines().collect(Collectors.joining());
                    Uid uid = new Uid(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                    Client client = new Client(uid, Server.getName());
                    Routing.getInstance().addClient(client, Server.getUid(), 0);
                    MessageContainer message = new MessageContainer(jsonString, client);
                    MessageHandler.receive(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
