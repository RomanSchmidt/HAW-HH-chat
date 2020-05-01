/**
 * commands for all commands in the CLI
 * @todo think about being static
 */
public class Cli {
    Cli() {
        this._run();
    }

    /**
     * run, map the string to commands, execute commands, quit if the exit command is send
     */
    private void _run() {
        boolean doRun = true;
        this._printAllCommands();
        while(doRun) {
            String command = this._readNextCommand();
            CliCommand convertedCommand = this._convertCommand(command);
            if(convertedCommand == null) {
                System.err.printf("unknown command %s\n", command);
            } else {
                doRun = this._executeCommand(convertedCommand);
            }
        }
    }

    /**
     * make an output of all commands
     */
    private void _printAllCommands() {
        for (CliCommand command : CliCommand.values()) {
            System.out.println(command);
        }
    }

    // switch case with command executes and parameter check -> add to blocking queue
    private boolean _executeCommand(CliCommand command) {
        return true;
    }

    // convert the string to known commands from CliCommand or return null
    // make sure the parameters are set if needed (send -> who to -> which message...)
    private CliCommand _convertCommand(String command) {
        return CliCommand.Help;
    }

    // get command and parameters from system input
    private String _readNextCommand() {
        return "help";
    }
}
