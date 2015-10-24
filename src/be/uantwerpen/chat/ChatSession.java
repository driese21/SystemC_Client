package be.uantwerpen.chat;

import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public class ChatSession extends UnicastRemoteObject implements IChatSession {
    private Chat chat;
    private ArrayList<IChatParticipator> participators;
    private String host;
    private String chatName;

    public ChatSession() throws RemoteException {
        chat = new Chat();
        participators = new ArrayList<>();
    }

    public ChatSession(IChatParticipator participator) throws RemoteException {
        this();
        participators.add(participator);
        host = participator.getName();
    }

    @Override
    public synchronized boolean newMessage(Message message) throws RemoteException, InterruptedException {
        System.out.println(message.toString());
        chat.addMessage(message);
        return true;
    }

    @Override
    public boolean addParticipator(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator cp : participators)
            if (cp.getName().equalsIgnoreCase(participator.getName())) return true; //already in chat
        //not in the list yet, so continue adding the new participator
        participators.add(participator);
        return true;
    }

    @Override
    public String getChatName() throws RemoteException {
        return chatName;
    }

    @Override
    public void setChatName(String chatName) throws RemoteException {
        this.chatName = chatName;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
