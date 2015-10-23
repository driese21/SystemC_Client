package be.uantwerpen.chat;

import java.rmi.RemoteException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Dries on 16/10/2015.
 */
public class Chat {
    private ArrayBlockingQueue<Message> messages;
    private boolean readLast;

    public Chat() {
        this.messages = new ArrayBlockingQueue<>(100);
    }

    public synchronized void addMessage(Message message) throws InterruptedException, RemoteException {
        messages.put(new Message(message));
    }

    private void updateClient() {
        System.out.println("Updating client ... (this is a lie)");
    }
}
