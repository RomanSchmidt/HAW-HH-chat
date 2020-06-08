package chat.communication;

import chat.message.MessageContainer;
import chat.routing.Routing;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender implements Runnable {

    private final MessageContainer _message;

    public Sender(MessageContainer message) {
        this._message = message;
    }

    @Override
    public void run() {
        System.out.println("send: " + this._message.getJsonString() + "; to: " + this._message.getClient().getUid() + "(" + this._message.getClient().getName() + ")");
        try (Socket socket = new Socket(this._message.getClient().getUid().getIp(), this._message.getClient().getUid().getPort())) {
            socket.setSoTimeout(2 * 1000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(this._message.getJsonString());
        } catch (UnknownHostException e) {
            System.err.println("not able to connect to host: " + this._message.getClient().getUid() + " (" + this._message.getClient().getName() + ")");
            Routing.getInstance().removeClient(this._message.getClient(), true);
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
