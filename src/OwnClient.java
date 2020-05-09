import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OwnClient extends Client {
    OwnClient(String ip, int port) {
        super(ip, port);
        this._connect();
    }

    private void _connect() {

        try (Socket server = new Socket(this._ip, this._port)) {
            Scanner in = new Scanner(server.getInputStream());
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);

            out.println("connecting");

            System.out.println("connected");

            //System.out.println(in.nextLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
