package chat.message;

import chat.communicator.Receiver;
import chat.communicator.Sender;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MessageHandler {

    private final static int _threadLimit = 8;

    private static final MessageHandler _instance = new MessageHandler();
    private final ThreadPoolExecutor _executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MessageHandler._threadLimit);

    public static void send(MessageContainer message) {
        MessageHandler._instance._executor.execute(new Sender(message));
    }

    public static void receive(MessageContainer message) {
        MessageHandler._instance._executor.execute(new Receiver(message));
    }
}
