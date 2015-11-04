package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
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
    private String username, hostName;
    private IChatSession chatSession;
    private ChatSession clonedChatSession;
    private ArrayList<IChatParticipator> otherParticipators;
    private IChatParticipator host; //chatsession host
    private final int maxRetries = 5;
    private int pushRetries;

    public ChatParticipator() throws RemoteException {
        this.otherParticipators = new ArrayList<>();
        this.clonedChatSession = new ChatSession();
    }

    public ChatParticipator(String username) throws RemoteException {
        this();
        this.username = username;
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
        this.hostName = host.getName();
        System.out.println("ADDING CHATSESSION, host is " + chatSession.getHost().getName());
        System.out.println("Chatsession name is " + chatSession.getChatName());
        this.clonedChatSession = new ChatSession(chatSession);
    }

    @Override
    public synchronized void notifyListener(ChatNotificationType cnt, IMessage msg) throws RemoteException {
        if (cnt == ChatNotificationType.NEWMESSAGE && msg != null) {
            System.out.println("Received new message from " + msg.getUsername() + ": " + msg.getMessage());
        } else System.out.println("Wrong notification, not a new chat...");
    }

    @Override
    public synchronized void notifyListener(ChatNotificationType cnt, IChatParticipator newParticipator) throws RemoteException {
        if (newParticipator != null) {
            if (cnt == ChatNotificationType.USERJOINED) {
                if (!participatorsExists(newParticipator)) {
                    otherParticipators.add(newParticipator);
                    System.out.println(newParticipator.getName() + " just joined");
                }
                else System.out.println("ChatParticipator " + newParticipator.getName() + " is already registered...");
            } else if (cnt == ChatNotificationType.USERLEFT) {
                if (participatorsExists(newParticipator)) otherParticipators.remove(newParticipator);
                else System.out.println("doesn't exists, can't remove...");
            }
        } else System.out.println("new participator is null?");
    }

    private boolean participatorsExists(IChatParticipator participator) throws RemoteException {
        for (IChatParticipator chatParticipator : otherParticipators) {
            if (chatParticipator.getName().equalsIgnoreCase(participator.getName())) return true;
        }
        return false;
    }

    @Override
    public String getName() throws RemoteException {
        return username;
    }

    /*@Override
    public void pushMessage(String msg) throws Exception {
        try {
            chatSession.newMessage(msg, getName());
            pushRetries = 0;
        } catch (RemoteException re) {
            if (pushRetries < maxRetries) {
                Thread.sleep(1000);
                pushRetries++;
                pushMessage(msg);
            } else { //still can't reach host, let's notify the server
                System.out.println("host left, trying to take over...");
                IChatParticipator server = null;
                //look for the server
                for (IChatParticipator participator : otherParticipators) {
                    if (participator.isServer()) { server = participator; break; }
                }
                if (server == null) throw new RemoteException("Server not found");
                if (!server.hostChat(this)) throw new RemoteException("Host was still reachable from server");
                for (int i=0;i<otherParticipators.size();i++) {
                    if (otherParticipators.get(i).getName().equalsIgnoreCase(hostName)) {
                        otherParticipators.remove(i);
                        break;
                    }
                }
                System.out.println("recovering cloned session");
                clonedChatSession.hostQuit(this);
                //update other participators that I am new host
                for (IChatParticipator other : otherParticipators) {
                    other.hostChanged(this,clonedChatSession);
                }
            }
        }
    }*/

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
        this.host = newHost;
        this.chatSession = newSession;
        this.chatSession.joinSession(this, true);
    }

    public ArrayList<IChatParticipator> getOtherParticipators() {
        return otherParticipators;
    }

    public IChatSession getChatSession() {
        return chatSession;
    }

    public String getHostName() {
        return hostName;
    }

    public ChatSession getClonedChatSession() {
        return clonedChatSession;
    }
}
