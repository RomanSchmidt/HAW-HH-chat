package chat.cli;

/**
 * list of possible commands
 */
public enum CliCommand {
    Help("/h", "help"),
    Select("/m", "messageTo"),
    SendAll("/a","sendToAll"),
    UserList("/u", "user"),
    Connect("/c", "connect"),
    Exit("/q", "quit"),
    Routing("/r", "routing");

    private final String _name;
    private String _description;

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
