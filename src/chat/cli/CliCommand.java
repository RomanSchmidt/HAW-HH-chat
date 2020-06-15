package chat.cli;

/**
 * list of possible commands
 */
public enum CliCommand {
    Help("/h", "help"),
    Select("/m", "send private message"),
    SendAll("/a","send message to all"),
    UserList("/u", "user"),
    Connect("/c", "connect"),
    Exit("/q", "quit"),
    Routing("/r", "routing"),
    Debug("/d", "toggle debug");

    private final String _name;
    private final String _description;

    CliCommand(String name, String description) {
        this._name = name;
        this._description = description;
    }

    public String getName() {
        return this._name;
    }

    public String getDescription() {
        return this._description;
    }

    public String toString() {
        return this.getName();
    }
}
