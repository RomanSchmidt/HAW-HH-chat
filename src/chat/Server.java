package chat;

import chat.cli.Cli;
import chat.client.Client;
import chat.communication.Communicator;
import chat.message.MessageContainer;
import chat.routing.Routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.stream.Collectors;

/**
 * start listen on a port
 * receive messages from clients
 */
public class Server implements Runnable {
    private static Uid _uid;
    private static String _name;
    private ServerSocket _socket;

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

    public static void quit() throws InterruptedException {
        throw new InterruptedException();
    }

    public void connect() {
        try {
            InetAddress addr = InetAddress.getByName(Server._uid.getIp());
            this._socket = new ServerSocket(Server._uid.getPort(), 0, addr);
        } catch (IOException e) {
            System.out.println(Cli.ANSI_RED + "connection invalid" + Cli.ANSI_RESET);
            Server.getUidFromCli();
            this.connect();
        }
    }

    /**
     * start server and listen on a specific port
     */
    @Override
    public void run() {
        while (true) {
            try (Socket clientSocket = this._socket.accept()) {
                BufferedReader messageBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("income: " + clientSocket.getInetAddress());
                String jsonString = messageBuffer.lines().collect(Collectors.joining());
                System.out.println("got jsonString: " + jsonString);
                MessageContainer message = new MessageContainer(jsonString);
                Communicator.receive(message);
            } catch (SocketException e) {
                System.err.println("socket error");
                this.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
