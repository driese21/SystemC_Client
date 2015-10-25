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
        addParticipator(participator);
        host = participator.getName();
    }

    @Override
    public synchronized boolean newMessage(Message message) throws RemoteException, InterruptedException {
        chat.addMessage(message);
        notifyParticipators();
        return true;
    }

    @Override
    public void notifyParticipators() throws RemoteException {
        for (IChatParticipator cp : participators) {
            cp.notifyListener();
        }
    }

    @Override
    public synchronized boolean addParticipator(IChatParticipator participator) throws RemoteException {
        System.out.println("*** PEOPLE IN CHAT ***");
        for (IChatParticipator cp : participators) {
            System.out.println(cp.getName());
        }
        System.out.println();
        for (IChatParticipator cp : participators)
            if (cp.getName().equalsIgnoreCase(participator.getName())) {
                System.out.println(cp.getName() + " already in chat...");
                return true; //already in chat
            }
        //not in the list yet, so continue adding the new participator
        participators.add(participator);
        System.out.println("Adding " + participator.getName());
        return true;
    }

    @Override
    public String getChatMessages() throws RemoteException {
        return chat.toString();
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
