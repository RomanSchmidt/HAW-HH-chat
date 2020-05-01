/**
 * dto for commands and their parameters
 */
public class CommandContainer {
    private final CliCommand _command;
    private final Parameter[] _params;

    CommandContainer(CliCommand command, Parameter[] params) {
        this._command = command;
        this._params = params;
    }

    public CliCommand getCommand() {
        return this._command;
    }

    public Parameter[] getParameter() {
        return this._params;
    }
}
