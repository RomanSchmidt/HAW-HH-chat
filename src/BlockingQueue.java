import java.util.ArrayList;

/**
 * a queue of commands, where all command executor threads can take the next one (thread save)
 */
public class BlockingQueue {
    private final ArrayList<CliCommand> _commands = new ArrayList<>();

    /**
     * @todo implement
     * add command to queue
     * wake up senders
     */
    public void enqueue(CliCommand command) {
        this._commands.add(command);
    }

    /**
     * returns the next command or null if nothing exists
     */
    public synchronized CliCommand getNext() {
        if (this._commands.size() == 0) {
            return null;
        }
        return this._commands.remove(0);
    }
}
