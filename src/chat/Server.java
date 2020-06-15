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
        Server._name = Cli.getParamString("name", "roman");
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
        Server._uid = new Uid(Cli.getParamString("ip", Server.getLocalHostAddress()), Cli.getParamInt("port", 8080));
    }

    public static String getLocalHostAddress() {
        InetAddress inetAddress = null;
        String hostAddress = "localhost";
        try {
            inetAddress = InetAddress.getLocalHost();
            hostAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return hostAddress;
    }

    public static void disconnect() {
        Routing.getInstance().getClient(Server.getUid()).disconnect(false, true);
    }

    public static void quit() throws InterruptedException {
        throw new InterruptedException();
    }

    public void connect() {
        try {
            InetAddress addr = InetAddress.getByName(Server._uid.getIp());
            this._socket = new ServerSocket(Server._uid.getPort(), 0, addr);
        } catch (IOException e) {
            Cli.printError("connection invalid");
            Routing.getInstance().removeClient(new Client(Server._uid, Server._name), false, false);
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
                Cli.printDebug("income", clientSocket.getInetAddress().toString());
                String jsonString = messageBuffer.lines().collect(Collectors.joining());
                Cli.printDebug("got jsonString", jsonString);
                MessageContainer message = new MessageContainer(jsonString);
                Communicator.receive(message);
            } catch (SocketException e) {
                Cli.printError("socket error");
                this.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
