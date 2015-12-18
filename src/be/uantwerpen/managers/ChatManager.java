package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.chat.ChatParticipatorKey;
import be.uantwerpen.chat.ChatSession;
import be.uantwerpen.client.Client;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.enums.ClientStatusType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.interfaces.managers.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;
import java.util.HashSet;

/**
 * Created by Dries on 3/11/2015.
 */
public class ChatManager implements IChatManager {
    private int pushRetries = 0;
    private int counter = 0;
    private UIManagerInterface uiManagerInterface;
    private Client client;

    public ChatManager(Client client) {
        this.client = client;
    }

    /**
     * Invites another user
     * @param friendName username of the other person
     * @throws RemoteException
     * @throws ClientNotOnlineException
     */
    @Override
    public ChatParticipator sendInvite(String friendName) throws RemoteException, UnknownClientException {
        ChatParticipator chatParticipator = new ChatParticipator(counter++,client.getUsername(), this);
        ChatSession chs = new ChatSession(chatParticipator);
        IChatSession serverSession = client.getClientSession().sendInvite(friendName, chs);
        //if serverSession is null, then we can store the ChatSession, which means it's an offline session
        if (serverSession == null) {
            chatParticipator.addChatSession(chs);
            client.addSession(chatParticipator);
            System.out.println("This is an online session, I'm hosting!");
            return chatParticipator;
        } else {
            chatParticipator.addChatSession(serverSession);
            serverSession.joinSession(chatParticipator, false);
            System.out.println("This is an offline session, server is hosting!");
            return chatParticipator;
        }
    }

    /**
     * Invites another client to an existing ChatSession
     * @param cp the participator that's in a given ChatSession
     * @param friendName the friend we want to invite
     * @throws RemoteException
     * @throws ClientNotOnlineException
     */
    @Override
    public boolean inviteToSession(ChatParticipator cp, String friendName) throws RemoteException, UnknownClientException {
        IChatSession chatSession = client.getClientSession().sendInvite(friendName, cp.getChatSession());
        if (chatSession == null) return true;
        else throw new RemoteException("This appears to be an offline ChatSession, can't invite other users");
    }

    /**
     * This gets called by ClientListener which got invoked by ClientSession on server
     * which received an invite request from another user, the method above
     * @param chatSession the ChatSession we will be chatting on
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean invite(IChatSession chatSession) throws RemoteException {
        ChatParticipator chatParticipator = new ChatParticipator(counter++, client.getUsername(), this);
        chatParticipator.addChatSession(chatSession);
        client.addSession(chatParticipator);
        chatSession.joinSession(chatParticipator, false);
        uiManagerInterface.openChat(chatParticipator);
        return true;
    }

    /**
     * This gets called from the UI and sends a message via the ChatParticipator
     * @param chatParticipator ChatParticipator in a specific session
     * @param msg message we want to send
     * @throws Exception
     */
    @Override
    public void pushMessage(ChatParticipator chatParticipator, String msg) throws RemoteException {
        String username = chatParticipator.getUserName();
        IChatSession iChatSession = chatParticipator.getChatSession();
        try {
            iChatSession.newMessage(msg, username);
            pushRetries = 0;
        } catch (RemoteException re) {
            int maxRetries = 5;
            if (pushRetries < maxRetries) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pushRetries++;
                pushMessage(chatParticipator, msg);
            } else {
                try {
                    tryRecoverChat(chatParticipator, msg);
                } catch (Exception e) {
                    if (e.getMessage().equalsIgnoreCase("Host was still reachable from server")) {
                        pushRetries = 0;
                        pushMessage(chatParticipator, msg);
                    }
                }
            }
        }
    }

    /**
     * This gets called from ChatParticipator which received a new notification from a ChatSession
     * @param cnt type of notification
     * @param msg reference to the message
     * @param participator which chatparticipator sent it
     * @throws Exception
     */
    @Override
    public void notifyView(ChatNotificationType cnt, IMessage msg, ChatParticipator participator) {
        if (cnt == ChatNotificationType.NEWMESSAGE) {
            uiManagerInterface.notifyView(cnt, msg, participator);
        } else if (cnt == ChatNotificationType.USERJOINED || cnt == ChatNotificationType.USERLEFT) {
            System.out.println("not yet implemented");
        }
    }

    /**
     * This gets invoked by ClientListener who received an update about the user's friends
     * @param cst The Client Status Type
     * @param friendName User friend's name
     */
    @Override
    public void notifyView(ClientStatusType cst, String friendName) {
        uiManagerInterface.notifyView(cst, friendName);
    }

    /**
     * This gets invoked when the client was not able to send a message after 5 retries (5 seconds)
     * It calls the server participator and verifies whether it's allowed to take over a session
     * After which it will notify other participators if it has taken over the session.
     * @param chatParticipator On which chatparticipator it should try to re-host the session
     * @throws Exception
     */
    @Override
    public void tryRecoverChat(ChatParticipator chatParticipator, String msg) throws Exception {
        System.out.println("host left, trying to take over...");
        IChatParticipator server = null;
        HashSet<ChatParticipatorKey> otherParticipators = chatParticipator.getOtherParticipators();
        //look for the server
        for (ChatParticipatorKey cpk : otherParticipators) {
            try {
                if (cpk.getParticipator().isServer()) { server = cpk.getParticipator(); break; }
            } catch (RemoteException re) {
                System.out.println("can't reach this client ...");
            }
        }
        if (server == null) throw new RemoteException("Server not found");
        if (!server.hostChat(chatParticipator)) throw new Exception("Host was still reachable from server");
        //remove chathost
        ChatParticipatorKey host = chatParticipator.getChatHost();
        otherParticipators.remove(host);
        System.out.println("recovering cloned session");
        chatParticipator.getClonedChatSession().hostQuit(host.getUserName(), new ChatParticipatorKey(chatParticipator.getUserName(), chatParticipator, true));
        //update other participators that I am new host
        for (ChatParticipatorKey other : otherParticipators) {
            other.getParticipator().hostChanged(chatParticipator,chatParticipator.getClonedChatSession());
        }
        //try again
        pushRetries = 0;
        pushMessage(chatParticipator, msg);
    }

    @Override
    public boolean leaveSession(ChatParticipator chatParticipator) throws RemoteException {
        IChatSession remoteSession = chatParticipator.getChatSession();
        if (remoteSession != null) {
            return remoteSession.leaveSession(chatParticipator.getUserName());
        }
        throw new RemoteException("Remote session does not exist");
    }

    public void setUiManagerInterface(UIManagerInterface uiManagerInterface) {
        this.uiManagerInterface = uiManagerInterface;
    }
}
