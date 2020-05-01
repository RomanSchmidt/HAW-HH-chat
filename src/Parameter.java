/**
 * dto for a parameter of a command
 */
public class Parameter {
    private final String _name;
    private final String _value;

    Parameter(String name, String value) {
        this._name = name;
        this._value = value;
    }

    public String getName() {
        return this._name;
    }

    public String getValue() {
        return this._value;
    }
}
