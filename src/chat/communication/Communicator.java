package chat.communication;

import chat.message.MessageContainer;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Communicator {

    private final static int _threadLimit = 8;

    private static final Communicator _instance = new Communicator();
    private final ThreadPoolExecutor _executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Communicator._threadLimit);

    public static void send(MessageContainer message) {
        Communicator._instance._executor.execute(new Sender(message));
    }

    public static void receive(MessageContainer message) {
        Communicator._instance._executor.execute(new Receiver(message));
    }

    public static ThreadPoolExecutor getExecutor() {
        return Communicator._instance._executor;
    }
}
