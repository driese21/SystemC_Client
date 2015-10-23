package be.uantwerpen.chat;

import be.uantwerpen.client.Client;
import be.uantwerpen.rmiInterfaces.IChatListener;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public class ChatSession extends UnicastRemoteObject implements IChatSession {
    private Chat chat;
    private ArrayList<IChatListener> listeners;

    public ChatSession() throws RemoteException {
        listeners = new ArrayList<>();
    }

    public ChatSession(ChatSession other, Chat chat) throws RemoteException {
        this();
        this.chat = chat;
    }

    @Override
    public synchronized boolean newMessage(Message msg) throws RemoteException, InterruptedException {
        System.out.println(msg);
        chat.addMessage(msg);
        return true;
    }

    public synchronized void addListener(IChatListener listener) {
        listeners.add(listener);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
