package be.uantwerpen.chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public class Chat extends UnicastRemoteObject {
    private ArrayList<Message> messages;
    private boolean readLast;

    public Chat() throws RemoteException {
        this.messages = new ArrayList<>(100);
    }

    public synchronized void addMessage(Message message) throws InterruptedException, RemoteException {
        messages.add(message);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        messages.forEach(sb::append);
        return sb.toString();
    }

    private void updateClient() {
        System.out.println("Updating client ... (this is a lie)");
    }
}
