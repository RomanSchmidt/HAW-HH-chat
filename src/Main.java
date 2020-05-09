public class Main {
    /**
     * @todo implement
     * get number of threads from params or set default to start the number of senders
     * get port from params or set default
     * starting all senders
     * start server
     * start cli to receive commands
     */
    public static void main(String[] args) {
        Cli cli = new Cli();

        Integer port = cli.getParamInt("port");

        Server server = new Server(port);
        server.start();
        try {
            cli.getCommands();
        } catch (InterruptedException e) {
            server.interrupt();
            System.exit(0);
        }
        //new OwnClient(ownServer);
    }

}
