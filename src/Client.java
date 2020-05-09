import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * handle clients and handle message send
 */
public class Client implements Receiver {
    /**
     * unique name of a client (ip + port as example)
     */
    protected final String _name;
    protected final String _ip;
    protected final int _port;
    private final Socket _socket;
    private final Scanner _in;
    private final PrintWriter _out;

    Client(String ip, int port, Socket socket) {
        this._ip = ip;
        this._port = port;
        this._name = ip + ":" + port;
        this._socket = socket;

        Scanner in = null;
        PrintWriter out = null;
        if (socket != null) {
            try {
                in = new Scanner(socket.getInputStream());
                String message = in.nextLine();
                // @todo send / provide this message to the target
                System.out.println("got message from client (" + this._name + ") :" + message);
            } catch (IOException e) {
                System.err.println("error on create socket in: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("error on create socket out: " + e.getMessage());
                e.printStackTrace();
            }
        }
        this._in = in;
        this._out = out;
    }

    Client(String ip, int port) {
        this(ip, port, null);
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
     * socket != null => send
     * socket == null => add the message to the blocking queue
     */
    @Override
    public void sendMessage(Message message) {
        if (this._socket == null) {
            // @todo add to the queue
        } else {
            this._out.println(message.getMessage());
        }
    }

    /**
     * @todo send disco message
     */
    public void disconnect() {

    }
}
