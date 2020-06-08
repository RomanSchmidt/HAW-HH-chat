package chat;

import chat.cli.Cli;

public class Main {

    public static void main(String[] args) {
        Server server  =new Server();
        server.connect();

        Thread serverTh = new Thread(server);
        serverTh.start();
        try {
            Cli.getCommands();
        } catch (InterruptedException e) {
            serverTh.interrupt();
        }
    }
}
