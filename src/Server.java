import java.util.HashMap;

/**
 * start listen on a port
 * receive messages from clients
 * send a message to a known client
 */
public class Server implements Receiver {
    private final HashMap<String, Client> _clients = new HashMap<>();
    private final int _port;
    private String _ip;

    Server(Integer port) {
        this._port = port;
        this._setOwnIp();
        this._create();
    }

    /**
     * @todo implement
     * figure out your own ip
     */
    private void _setOwnIp() {
        this._ip = "";
    }

    /**
     * @todo implement
     * start server and listen on a specific port
     */
    private void _create() {

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
