package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Dries on 23/10/2015.
 */
public class ChatParticipator extends UnicastRemoteObject implements IChatParticipator {
    private int id;
    private String username, hostName, chatName;
    private IChatSession chatSession;
    private ChatSession clonedChatSession;
    private ArrayList<IChatParticipator> otherParticipators;
    private IChatParticipator host; //chatsession host
    private IChatManager chatManager;

    public ChatParticipator() throws RemoteException {
        this.otherParticipators = new ArrayList<>();
        this.clonedChatSession = new ChatSession();
    }

    public ChatParticipator(int id, String username, IChatManager chatManager) throws RemoteException {
        this();
        this.id = id;
        this.username = username;
        this.chatManager = chatManager;
    }

    public int getId() {
        return id;
    }

    /**
     * This functions gets invoked on the invited client
     * @param chatSession
     * @throws RemoteException
     */
    @Override
    public void addChatSession(IChatSession chatSession) throws RemoteException {
        this.chatSession = chatSession;
        this.host = chatSession.getHost();
        this.hostName = host.getUserName();
        this.chatName = chatSession.getChatName();
        this.clonedChatSession = new ChatSession(chatSession);
    }

    /**
     * Gets invoked by the ChatSession if a new message has been sent
     * @param cnt Which type of notification
     * @param msg A reference to the Message
     * @throws RemoteException
     */
    @Override
    public synchronized void notifyListener(ChatNotificationType cnt, IMessage msg) throws RemoteException {
        if (cnt == ChatNotificationType.NEWMESSAGE && msg != null) {
            chatManager.notifyView(cnt, msg, this);
        }
    }

    /**
     * Gets invoked by the ChatSession if a user has joined or left
     * @param cnt Type of notification
     * @param newParticipator The specific participator
     * @throws RemoteException
     */
    @Override
    public synchronized void notifyListener(ChatNotificationType cnt, IChatParticipator newParticipator) throws RemoteException {
        if (newParticipator != null) {
            if (cnt == ChatNotificationType.USERJOINED) {
                if (!participatorsExists(newParticipator)) {
                    otherParticipators.add(newParticipator);
                    System.out.println(newParticipator.getUserName() + " just joined");
                }
            } else if (cnt == ChatNotificationType.USERLEFT) {
                if (participatorsExists(newParticipator)) otherParticipators.remove(newParticipator);
                else System.out.println("doesn't exists, can't remove...");
            }
        } else System.out.println("new participator is null?");
    }

    /**
     * Checks if a participators exists in the participator list
     * @param participator participator to check
     * @return true if the participator is already in the list, false if it's not
     * @throws RemoteException
     */
    private boolean participatorsExists(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator chatParticipator : otherParticipators) {
            if (chatParticipator.getUserName().equalsIgnoreCase(participator.getUserName())) return true;
        }
        return false;
    }

    /**
     * @return Username of this ChatParticipator
     * @throws RemoteException
     */
    @Override
    public String getUserName() throws RemoteException {
        return username;
    }

    @Override
    public String getChatName() throws RemoteException {
        return chatSession.getChatName();
    }

    @Override
    public boolean isServer() throws RemoteException {
        return false;
    }

    /**
     * Will throw exception when participator is no longer alive
     * @return will return true when participator is alive, otherwise throw error
     * @throws RemoteException
     */
    @Override
    public boolean alive() throws RemoteException {
        return true;
    }

    /**
     * Server-only functionality
     * @param newHost
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean hostChat(IChatParticipator newHost) throws RemoteException {
        return false;
    }

    /**
     * Will update the local clone of the chat session based on the update
     * @param cnt notification type
     * @throws RemoteException
     */
    @Override
    public synchronized void cloneSession(ChatNotificationType cnt) throws RemoteException {
        if (clonedChatSession == null) return; //still need to capture this
    }

    /**
     * Update the chatsession to the newly hosted one
     * @param newHost the chatparticipator that will now host the chatsession
     * @param newSession a reference to the new chatsession of the new host
     * @throws RemoteException
     */
    @Override
    public void hostChanged(IChatParticipator newHost, IChatSession newSession) throws RemoteException {
        addChatSession(newSession);
        this.chatSession.joinSession(this);
    }

    public ArrayList<IChatParticipator> getOtherParticipators() {
        return otherParticipators;
    }

    public IChatSession getChatSession() {
        return chatSession;
    }

    /**
     * The hostname is stored redundantly in order to remove host when it has dropped the connection
     * @return The name of the host ChatParticipator (who is hosting ChatSession)
     */
    public String getHostName() {
        return hostName;
    }

    public ChatSession getClonedChatSession() {
        return clonedChatSession;
    }

    @Override
    public String toString() {
        return chatName;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ChatParticipator b = (ChatParticipator) obj;
        return username.equalsIgnoreCase(b.username);
    }
}
