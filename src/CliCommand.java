/**
 * list of possible commands
 *
 * @todo also add params information and add them to the toString method
 */
public enum CliCommand {
    Help("help"),
    Send("send"),
    Connect("connect");

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
