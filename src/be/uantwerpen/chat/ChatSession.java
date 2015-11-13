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
        chat.addMessage(message);
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
        for (IChatParticipator participator : participators) {
            participator.notifyListener(cnt, msg);
        }
    }

    /**
     * Notifies all participators that a user has joined/left
     * @param cnt Type of notification
     * @param newParticipator the user who just joined/left
     * @throws RemoteException
     */
    @Override
    public void notifyParticipators(ChatNotificationType cnt, IChatParticipator newParticipator) throws RemoteException {
        for (IChatParticipator participator : participators) {
            participator.notifyListener(cnt, newParticipator);
        }
    }

    /**
     * Used to check if a participator is already in the session
     * @param participator Said participator
     * @return true if the user is already in the list, false if it's not
     * @throws RemoteException
     */
    @Override
    public synchronized boolean joinSession(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator cp : participators)
            if (cp.getName().equalsIgnoreCase(participator.getName()))
                return true; //already in chat
        return false;
    }

    /**
     * This gets invoked by a ChatParticipator, who wants to join this ChatSession
     * @param participator A reference to said ChatParticipator
     * @param silent If false, it will notify all other users (visibly)
     * @return true if user successfully joined, false if something went wrong
     * @throws RemoteException
     */
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
    public void chooseChatName() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        for (IChatParticipator participator : participators) {
            if (!participator.isServer()) sb.append(participator.getName());
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

    @Override
    public void setParticipators(ArrayList<IChatParticipator> participators) throws RemoteException {
        this.participators = participators;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }


}
