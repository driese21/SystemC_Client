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
    private IChatParticipator host;
    private String chatName;

    public ChatSession() throws RemoteException {
        chat = new Chat();
        participators = new ArrayList<>();
    }

    public ChatSession(IChatSession chatSession) throws RemoteException {
        this.chat = new Chat();
        this.participators = chatSession.getOtherParticipators();
        this.host = chatSession.getHost();
        this.chatName = chatSession.getChatName();
    }

    public ChatSession(IChatParticipator participator) throws RemoteException {
        this();
        joinSession(participator, true);
        host = participator;
    }

    @Override
    public synchronized boolean newMessage(String msg, String username) throws RemoteException, InterruptedException {
        Message message = new Message(msg, username);
        chat.addMessage(message);
        notifyParticipators(ChatNotificationType.NEWMESSAGE, message);
        return true;
    }

    @Override
    public void notifyParticipators(ChatNotificationType cnt, Message msg) throws RemoteException {
        for (IChatParticipator participator : participators) {
            participator.notifyListener(cnt, msg);
        }
    }

    @Override
    public void notifyParticipators(ChatNotificationType cnt, IChatParticipator newParticipator) throws RemoteException {
        for (IChatParticipator participator : participators) {
            participator.notifyListener(cnt, newParticipator);
        }
    }

    /*@Override
    public void notifyParticipators(ChatNotificationType cnt) throws RemoteException {
        for (IChatParticipator participator : participators) {
            participator.notifyListener(cnt);
        }
        *//*participators.forEach((participator) -> {
            try { participator.notifyListener(cnt); }
            catch (RemoteException e) { e.printStackTrace(); }
        });*//*
    }*/

    @Override
    public synchronized boolean joinSession(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator cp : participators)
            if (cp.getName().equalsIgnoreCase(participator.getName()))
                return true; //already in chat
        return false;
    }

    @Override
    public synchronized boolean joinSession(IChatParticipator participator, boolean silent) throws RemoteException {
        if (joinSession(participator)) return true;
        System.out.println(participator.getName() + (silent ? " is trying to sneak in..." : " is joining"));
        //not in the list yet, so continue adding the new participator
        if (participators.add(participator)) {
            notifyParticipators(ChatNotificationType.USERJOINED, participator);
            return true;
        } else return false;
    }

    @Override
    public IChatParticipator getHost() throws RemoteException {
        return host;
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

    @Override
    public boolean hostQuit(IChatParticipator newHost) throws RemoteException {
        if (!participators.remove(host)) return false;
        host = newHost;
        //notifyParticipators(ChatNotificationType.NEWHOST);
        return true;
    }

    @Override
    public ArrayList<IChatParticipator> getOtherParticipators() throws RemoteException {
        System.out.println("Participators length: " + participators.size());
        return participators;
    }

    @Override
    public void setParticipators(ArrayList<IChatParticipator> participators) throws RemoteException {
        this.participators = participators;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }


}
