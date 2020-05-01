import java.util.HashMap;

/**
 * handle the table
 */
public class RoutingTable {
    // To -> Via -> costs
    private final HashMap<Client, HashMap<Client, Integer>> _table = new HashMap<>();

    /**
     * @todo implement
     */
    public void analyzeForeignTable(RoutingTable table) {

    }

    /**
     * @todo implement
     * update the table with all information it needs to be routed correctly
     */
    public void addOwnClient(Client client) {

    }

    /**
     * @todo implement
     * update table
     */
    public void removeOwnClient(Client client) {

    }

    /**
     * get a clone of the table
     */
    @SuppressWarnings("unchecked")
    public HashMap<Client, HashMap<Client, Integer>> getTable() {
        return (HashMap<Client, HashMap<Client, Integer>>) this._table.clone();
    }
}
