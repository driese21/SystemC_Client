package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by Dries on 16/10/2015.
 */
public class ChatSession extends UnicastRemoteObject implements IChatSession {
    private HashSet<ChatParticipatorKey> participators;
    private ChatParticipatorKey chatHost;
    private String chatName;
    private ArrayList<Message> messages;

    public ChatSession() throws RemoteException {
        this.participators = new HashSet<>();
        this.messages = new ArrayList<>();
    }

    public ChatSession(IChatSession chatSession) throws RemoteException {
        this();
        this.chatHost = new ChatParticipatorKey(chatSession.getHost().getUserName(), chatSession.getHost(), true);
        for (IChatParticipator chatParticipator : chatSession.getOtherParticipators()) {
            this.participators.add(new ChatParticipatorKey(chatParticipator.getUserName(), chatParticipator, false));
        }
        this.chatName = chatSession.getChatName();
    }

    /**
     * This gets invoked on the hosting client
     * @param participator
     * @throws RemoteException
     */
    public ChatSession(IChatParticipator participator) throws RemoteException {
        this();
        this.chatHost = new ChatParticipatorKey(participator.getUserName(), participator, true);
        joinSession(chatHost);
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
     * @param cpk the user who just joined/left
     * @throws RemoteException
     */
    @Override
    public void notifyParticipators(ChatNotificationType cnt, ChatParticipatorKey cpk) throws RemoteException {
        if (cpk.equals(chatHost)) return;
        if (cpk.getParticipator() != null & cpk.getParticipator().isServer()) return;
        new Thread(new DeliveryAgent(participators, cpk, cnt)).start();
        chooseChatName();
    }

    /**
     * Used to check if a participator is already in the session, if not add him
     * @param participator Said participator
     * @return true if the user is already in the list or successfully joined, false if it's not
     * @throws RemoteException
     */
    @Override
    public synchronized boolean joinSession(IChatParticipator participator, boolean host) throws RemoteException {
        ChatParticipatorKey cpk = new ChatParticipatorKey(participator.getUserName(), participator, host);
        if (participators.contains(cpk)) return true; //already in chat
        participators.add(cpk);
        notifyParticipators(ChatNotificationType.USERJOINED, cpk);
        return true;
    }

    /**
     * Gets invoked by the hosting client
     * @param cpk Is ChatHost
     * @return true if successfully joined
     * @throws RemoteException
     */
    @Override
    public synchronized boolean joinSession(ChatParticipatorKey cpk) throws RemoteException {
        return joinSession(cpk.getParticipator(), true);
    }

    @Override
    public boolean leaveSession(String username) throws RemoteException {
        for (ChatParticipatorKey cpk : participators) {
            if (cpk.getUserName().equalsIgnoreCase(username)) {
                notifyParticipators(ChatNotificationType.USERLEFT, cpk);
                return true;
            }
        }
        return false;
    }

    @Override
    public IChatParticipator getHost() throws RemoteException {
        return chatHost.getParticipator();
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
        for (ChatParticipatorKey cpk : participators) {
            IChatParticipator participator = cpk.getParticipator();
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
    public boolean hostQuit(String oldHost, ChatParticipatorKey newHost) throws RemoteException {
        Iterator it = participators.iterator();
        while (it.hasNext()) {
            ChatParticipatorKey cpk = (ChatParticipatorKey) it.next();
            if (cpk.getUserName().equalsIgnoreCase(oldHost)) {
                it.remove();
                break;
            }
        }
        participators.remove(oldHost);
        this.chatHost = newHost;
        return true;
    }

    @Override
    public HashSet<IChatParticipator> getOtherParticipators() throws RemoteException {
        HashSet<IChatParticipator> parts = new HashSet<>(participators.size());
        participators.forEach(cpk -> parts.add(cpk.getParticipator()));
        return parts;
    }

    @Override
    public ArrayList<IMessage> getMessages() throws RemoteException {
        return new ArrayList<>(messages);
    }
}
