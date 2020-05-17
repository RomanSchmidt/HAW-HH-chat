package chat.cli;

/**
 * list of possible commands
 */
public enum CliCommand {
    Help("/help"),
    Select("/messageTo"),
    UserList("/user"),
    Send("/send"),
    Connect("/connect"),
    Exit("/exit");

    private final String _name;

    CliCommand(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public String toString() {
        return this.getName();
    }
}
