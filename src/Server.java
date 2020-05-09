import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * start listen on a port
 * receive messages from clients
 * send a message to a known client
 */
public class Server extends Thread implements Receiver {
    private final HashMap<String, Client> _clients = new HashMap<>();
    private final int _port;
    private String _ip;
    private ServerSocket _serverSocket;

    Server(Integer port) {
        this._port = port;
        this._setOwnIp();
        this._create();
    }

    @Override
    public void run() {
        try {
            /*final Socket clientSocket = new Socket(this._ip, this._port);
            System.out.println("warte auf client!");
            System.out.println(clientSocket.getInputStream().read());
            System.out.println("cient hat geschrieben!");*/
            //this._serverSocket.setSoTimeout(60000); // Timeout
            System.out.println("waiting for connection");
            Socket clientSocket = this._serverSocket.accept();
            System.out.println("client connected");
            Client client = new Client(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), clientSocket);
            this._clients.put(client.getName(), client);
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Socket closed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * try {
     * this._serverSocket.setSoTimeout(60000); // Timeout
     * Socket clientSocket = this._serverSocket.accept();
     * Client client = new Client(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
     * this._clients.put(client.getName(), client);
     * } catch (InterruptedIOException | SocketException e) {
     * System.err.println("Socket closed");
     * } catch (IOException e) {
     * System.err.println("Socket invalid: " + e.getMessage());
     * e.printStackTrace();
     * }
     */

    public String getIp() {
        return this._ip;
    }

    public int getPort() {
        return this._port;
    }

    /**
     * set local ip
     */
    private void _setOwnIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
            // could be used for name
            System.out.println("Host Name:- " + inetAddress.getHostName());
            this._ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("not able to resolve local ip: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @todo implement
     * start server and listen on a specific port
     */
    private void _create() {
        try {
            this._serverSocket = new ServerSocket(this._port);
        } catch (IOException e) {
            System.out.println("not able to open port (" + this._port + ") " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @todo implement
     * print out the message, if message.to.name is this.name
     * add message to queue
     */
    private void _handleReceivedMessage(Client from, Message message) {

    }

    /**
     * @todo implement
     * @todo delete, throw or exit if add a already known client?
     * see if this client is already known -> delete it first / throw error ?
     * create a new client object
     * add client to the hash with ip + port as string key
     * get his routing table
     * update routing table
     */
    private void addClient() {

    }

    /**
     * @todo implement
     * client disconnected or any other reason to kick
     * drop client from hash map
     * update routing table
     */
    private void removeClient() {

    }

    /**
     * return a list of client names
     */
    public String[] getAllClients() {
        String[] clients = new String[this._clients.size()];

        int i = 0;
        for (String clientName : this._clients.keySet()) {
            clients[i++] = clientName;
        }

        return clients;
    }

    /**
     * @todo implement
     * send message to a client
     */
    @Override
    public void sendMessage(Message message) {

    }
}
