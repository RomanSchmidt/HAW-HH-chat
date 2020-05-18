package chat;

import chat.cli.Cli;

public class Main {

    public static void main(String[] args) {
        Thread server = new Thread(new Server());
        server.start();
        try {
            Cli.getCommands();
        } catch (InterruptedException e) {
            server.interrupt();
        }
    }
}
