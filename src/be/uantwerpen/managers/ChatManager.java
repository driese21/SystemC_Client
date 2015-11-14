package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.chat.ChatSession;
import be.uantwerpen.client.Client;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.enums.ClientStatusType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.interfaces.IChatManager;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;
import java.util.ArrayList;

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
    public ChatParticipator sendInvite(String friendName) throws RemoteException, ClientNotOnlineException {
        ChatParticipator chatParticipator = new ChatParticipator(counter++,client.getUsername(), this);
        ChatSession chs = new ChatSession(chatParticipator);
        chatParticipator.addChatSession(chs);
        if (client.getClientSession().sendInvite(friendName, chs)) {
            //if invite was successfull, remember it, otherwise garbage
            client.addSession(chatParticipator);
            return chatParticipator;
        }
        return null;
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
        String username = chatParticipator.getName();
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
                    tryRecoverChat(chatParticipator);
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
    private void tryRecoverChat(ChatParticipator chatParticipator) throws Exception {
        System.out.println("host left, trying to take over...");
        IChatParticipator server = null;
        ArrayList<IChatParticipator> otherParticipators = chatParticipator.getOtherParticipators();
        //look for the server
        for (IChatParticipator participator : otherParticipators) {
            if (participator.isServer()) { server = participator; break; }
        }
        if (server == null) throw new RemoteException("Server not found");
        if (!server.hostChat(chatParticipator)) throw new Exception("Host was still reachable from server");
        for (int i=0;i<otherParticipators.size();i++) {
            if (otherParticipators.get(i).getName().equalsIgnoreCase(chatParticipator.getHostName())) {
                otherParticipators.remove(i);
                break;
            }
        }
        System.out.println("recovering cloned session");
        chatParticipator.getClonedChatSession().hostQuit(chatParticipator);
        //update other participators that I am new host
        for (IChatParticipator other : otherParticipators) {
            other.hostChanged(chatParticipator,chatParticipator.getClonedChatSession());
        }
    }

    public void setUiManagerInterface(UIManagerInterface uiManagerInterface) {
        this.uiManagerInterface = uiManagerInterface;
    }
}
