/**
 * get next command from blocking queue
 * execute command
 * wait to be woken up
 */
public class Sender extends Thread {

    private final BlockingQueue _queue;

    Sender(BlockingQueue queue){
        this._queue = queue;
    }

    private CliCommand _getNextCommand() {
        return this._queue.getNext();
    }
}
