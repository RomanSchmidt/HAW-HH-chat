package chat;

import chat.cli.Cli;

public class Main {

    public static void main(String[] args) {
        Cli cli = new Cli();

        String name = cli.getParamString("name");
        Integer port = cli.getParamInt("port");

        Thread server = new Thread(new Server(port, name));
        server.start();
        try {
            cli.getCommands();
        } catch (InterruptedException e) {
            server.interrupt();
        }
        System.exit(0);
    }
}
