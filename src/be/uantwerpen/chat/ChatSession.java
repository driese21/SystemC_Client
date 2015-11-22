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
    private ArrayList<IChatParticipator> participators;
    private IChatParticipator host;
    private String chatName;
    private ArrayList<Message> messages;

    public ChatSession() throws RemoteException {
        this.participators = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public ChatSession(IChatSession chatSession) throws RemoteException {
        this.participators = chatSession.getOtherParticipators();
        this.host = chatSession.getHost();
        this.chatName = chatSession.getChatName();
    }

    public ChatSession(IChatParticipator participator) throws RemoteException {
        this();
        joinSession(participator);
        this.host = participator;
    }

    /**
     * This gets called by anyone who wants to send a message
     * @param msg The message
     * @param username The username of the participator
     * @return true if the message has been sent
     * @throws RemoteException
     */
    @Override
    public synchronized boolean newMessage(String msg, String username) throws RemoteException {
        Message message = new Message(msg, username);
        messages.add(message);
        notifyParticipators(ChatNotificationType.NEWMESSAGE, message);
        return true;
    }

    /**
     * Notifies all participators that a new message has arrived
     * @param cnt Type of notification
     * @param msg A message
     * @throws RemoteException
     */
    @Override
    public void notifyParticipators(ChatNotificationType cnt, Message msg) throws RemoteException {
        new Thread(new DeliveryAgent(participators, msg, cnt)).start();
    }

    /**
     * Notifies all participators that a user has joined/left
     * @param cnt Type of notification
     * @param newParticipator the user who just joined/left
     * @throws RemoteException
     */
    @Override
    public void notifyParticipators(ChatNotificationType cnt, IChatParticipator newParticipator) throws RemoteException {
        new Thread(new DeliveryAgent(participators, newParticipator, cnt)).start();
        chooseChatName();
    }

    /**
     * Used to check if a participator is already in the session, if not add him
     * @param participator Said participator
     * @return true if the user is already in the list or successfully joined, false if it's not
     * @throws RemoteException
     */
    @Override
    public synchronized boolean joinSession(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator cp : participators)
            if (cp.getUserName().equalsIgnoreCase(participator.getUserName()))
                return true; //already in chat
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
    public String getChatName() throws RemoteException {
        return chatName;
    }

    @Override
    public void setChatName(String chatName) throws RemoteException {
        this.chatName = chatName;
    }

    @Override
    public void chooseChatName() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        for (IChatParticipator participator : participators) {
            if (!participator.isServer()) sb.append(participator.getUserName());
        }
        setChatName(sb.toString());
    }

    /**
     * Gets invoked by another ChatParticipator who is re-hosting the ChatSession
     * @param newHost A reference to the new host
     * @return true if host has been changed, false if it failed to remove previous host
     * @throws RemoteException
     */
    @Override
    public boolean hostQuit(IChatParticipator newHost) throws RemoteException {
        if (!participators.remove(host)) return false;
        host = newHost;
        return true;
    }

    @Override
    public ArrayList<IChatParticipator> getOtherParticipators() throws RemoteException {
        return participators;
    }
}
