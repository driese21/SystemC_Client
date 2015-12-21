package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A chat participator is a client who has joined a chat session
 * Created by Dries on 23/10/2015.
 *
 */
public class ChatParticipator extends UnicastRemoteObject implements IChatParticipator {
    private int id;
    private String username, chatName;
    private IChatSession chatSession;
    private ChatSession clonedChatSession;
    private HashSet<ChatParticipatorKey> otherParticipators;
    private ChatParticipatorKey chatHost;
    private IChatManager chatManager;

    public ChatParticipator() throws RemoteException {
        this.otherParticipators = new HashSet<>();
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
     * Invites a user to a chatSession
     * @param chatSession
     * @throws RemoteException
     */
    @Override
    public void addChatSession(IChatSession chatSession) throws RemoteException {
        this.chatSession = chatSession;
        this.chatHost = new ChatParticipatorKey(chatSession.getHost().getUserName(), chatSession.getHost(), false);
        this.chatName = chatSession.getChatName();
        this.clonedChatSession = new ChatSession(chatSession);
    }

    /**
     * Gets invoked by the ChatSession if a new message has been sent
     * @param msg A reference to the Message
     * @throws RemoteException
     */
    @Override
    public synchronized void notifyListener(IMessage msg) throws RemoteException {
        if (msg != null) {
            chatManager.notifyView(ChatNotificationType.NEWMESSAGE, msg, this);
        }
    }

    /**
     * Gets invoked by the ChatSession if a user has joined
     * @param chatParticipator The specific participator
     * @param host defines if this participator is the host
     * @throws RemoteException
     */
    @Override
    public synchronized void notifyListener(IChatParticipator chatParticipator, boolean host) throws RemoteException {
        if (chatParticipator != null) {
            ChatParticipatorKey cpk = new ChatParticipatorKey(chatParticipator.getUserName(), chatParticipator, host);
            if (!participatorsExists(cpk.getUserName())) {
                otherParticipators.add(cpk);
            }
        } else System.out.println("new participator is null?");
    }

    /**
     * Gets invoked by the ChatSession if a user has left the session
     * @param userName Username of user who has left
     * @throws RemoteException
     */
    @Override
    public void notifyListener(String userName) throws RemoteException {
        if (participatorsExists(userName)) {
            Iterator it = otherParticipators.iterator();
            while (it.hasNext()) {
                ChatParticipatorKey cpk = (ChatParticipatorKey) it.next();
                if (cpk.getUserName().equalsIgnoreCase(userName)) {
                    it.remove();
                    break;
                }
            }
        }
    }

    /**
     * Checks if a participators exists in the participator list
     * @param username participator to check
     * @return true if the participator is already in the list, false if it's not
     * @throws RemoteException
     */
    private boolean participatorsExists(String username) throws RemoteException {
        for (ChatParticipatorKey cpk : otherParticipators) if (cpk.getUserName().equalsIgnoreCase(username)) return true;
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
     * Update the ChatSession to the newly hosted one, this gets called by the new host
     * @param newHost the ChatParticipator that will now host the ChatSession
     * @param newSession a reference to the new chatsession of the new host
     * @throws RemoteException
     */
    @Override
    public void hostChanged(IChatParticipator newHost, IChatSession newSession) throws RemoteException {
        addChatSession(newSession);
        this.chatSession.joinSession(this, false);
    }

    public HashSet<ChatParticipatorKey> getOtherParticipators() {
        return otherParticipators;
    }

    @Override
    public IChatSession getChatSession() {
        return chatSession;
    }

    /**
     * The hostname is stored redundantly in order to remove host when it has dropped the connection
     * @return The name of the host ChatParticipator (who is hosting ChatSession)
     */
    public ChatParticipatorKey getChatHost() {
        return chatHost;
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
        if (this == obj) return true;
        ChatParticipator b = (ChatParticipator) obj;
        return username.equalsIgnoreCase(b.username);
    }
}
