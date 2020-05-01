/**
 * handle clients and handle message send
 */
public class Client implements Receiver {
    /**
     * unique name of a client (ip + port as example)
     */
    private final String _name;
    private final String _ip;
    private final int _port;

    Client(String ip, int port) {
        this._ip = ip;
        this._port = port;
        this._name = ip + ":" + port;
    }

    public String getName() {
        return this._name;
    }

    public String getIp() {
        return this._ip;
    }

    public int getPort() {
        return this._port;
    }

    /**
     * @todo implement
     * add the message to the blocking queue
     */
    @Override
    public void sendMessage(Message message) {

    }
}
