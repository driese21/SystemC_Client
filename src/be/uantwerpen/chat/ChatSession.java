package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
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
        joinSession(participator);
        host = participator.getName();
    }

    @Override
    public synchronized boolean newMessage(Message message) throws RemoteException, InterruptedException {
        chat.addMessage(message);
        notifyParticipators(ChatNotificationType.NEWMESSAGE);
        return true;
    }

    @Override
    public void notifyParticipators(ChatNotificationType cnt) throws RemoteException {
        participators.forEach((participator) -> {
            try { participator.notifyListener(cnt); }
            catch (RemoteException e) { e.printStackTrace(); }
        });
    }

    @Override
    public synchronized boolean joinSession(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator cp : participators)
            if (cp.getName().equalsIgnoreCase(participator.getName()))
                return true; //already in chat
        //not in the list yet, so continue adding the new participator
        if (participators.add(participator)) {
            notifyParticipators(ChatNotificationType.USERJOINED);
            return true;
        } else return false;
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
