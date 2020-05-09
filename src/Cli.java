import java.util.Scanner;

/**
 * commands for all commands in the CLI
 *
 * @todo think about being static
 */
public class Cli {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private OwnClient _ownClient;

    Cli() {

    }

    /**
     * run, map the string to commands, execute commands, quit if the exit command is send
     */
    public void getCommands() throws InterruptedException {
        boolean doRun = true;
        this._printAllCommands();
        while (doRun) {
            String command = this._readNextCommand();
            CliCommand convertedCommand = this._convertCommand(command);
            if (convertedCommand == null) {

                System.out.print(ANSI_RED + "unknown command: " + ANSI_RESET + command + "\n");
            } else {
                System.out.print("entered command: " + ANSI_GREEN + command + ANSI_RESET + "\n");
                // @todo get this from real params
                doRun = this._executeCommand(convertedCommand);
            }
        }
        /*for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getState() == Thread.State.RUNNABLE) {
                t.interrupt();
            }
        }*/
        throw new InterruptedException();
    }

    /**
     * make an output of all commands
     */
    private void _printAllCommands() {
        System.out.println("_______ALLOWED COMMANDS_______");
        for (CliCommand command : CliCommand.values()) {
            System.out.println(command);
        }
        System.out.println("______________________________");
    }

    // switch case with command executes and parameter check -> add to blocking queue
    private boolean _executeCommand(CliCommand command) {
        switch (command) {
            case Help:
                this._printAllCommands();
                return true;
            case Send:
                System.out.println(ANSI_YELLOW + "not implemented" + ANSI_RESET);
                break;
            case Connect:
                String ip = this.getParamString("ip");
                Integer port = this.getParamInt("port");
                this._connect(ip, port);
                return true;
            case Exit:
                System.out.println("exiting");
                return false;
        }
        System.err.println("can not find execution for commandContainer: " + command.getName());
        return false;
    }

    private void _connect(String ip, Integer port) {
        if (null != this._ownClient) {
            this._ownClient.disconnect();
        }
        this._ownClient = new OwnClient(ip, port);
    }

    public String getParamString(String name) {
        System.out.print(name + ": ");
        Scanner in = new Scanner(System.in);

        return in.next();
    }

    public Integer getParamInt(String name) {
        System.out.print(name + ": ");
        Scanner in = new Scanner(System.in);

        return  in.nextInt();
    }

    // convert the string to known commands from CliCommand or return null
    // make sure the parameters are set if needed (send -> who to -> which message...)
    private CliCommand _convertCommand(String commandString) {
        for (CliCommand command : CliCommand.values()) {
            if (command.getName().equals(commandString)) {
                return command;
            }
        }
        return null;
    }

    // get command and parameters from system input
    private String _readNextCommand() {
        System.out.print("enter command: ");
        return new Scanner(System.in).nextLine();
    }
}
