package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.chat.ChatSession;
import be.uantwerpen.client.Client;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 3/11/2015.
 */
public class ChatManager {
    private static final int MAXRETRIES = 5;
    private static int pushRetries = 0;
    private static int counter = 0;

    /**
     * Invites another user
     * @param friendName username of the other person
     * @throws RemoteException
     * @throws ClientNotOnlineException
     */
    public static int sendInvite(String friendName) throws RemoteException, ClientNotOnlineException {
        ChatParticipator chatParticipator = new ChatParticipator(counter++,Client.getInstance().getUsername());
        ChatSession chs = new ChatSession(chatParticipator);
        //chs.setChatName(Client.getInstance());
        chatParticipator.addChatSession(chs);
        if (Client.getInstance().getClientSession().sendInvite(friendName, chs)) {
            //if invite was successfull, remember it, otherwise garbage
            Client.getInstance().addSession(chatParticipator);
            return counter++;
        }
        return -1;
    }

    /**
     * This gets called by ChatInitiator which got invoked by ClientSession on server
     * which received an invite request from another user, the method above
     * @param chatSession the ChatSession we will be chatting on
     * @return
     * @throws RemoteException
     */
    public static boolean invite(IChatSession chatSession) throws RemoteException {
        System.out.println("[INVITED CLIENT]Client");
        ChatParticipator chatParticipator = new ChatParticipator(counter++, Client.getInstance().getUsername());
        chatParticipator.addChatSession(chatSession);
        System.out.println("[INVITED CLIENT] Adding myself to participator list " + chatSession.joinSession(chatParticipator, false));
        Client.getInstance().addSession(chatParticipator);
        return true;
    }

    /**
     * This gets called from the UI and sends a message via the ChatParticipator
     * @param chatParticipator ChatParticipator in a specific session
     * @param msg message we want to send
     * @throws Exception
     */
    public static void pushMessage(ChatParticipator chatParticipator, String msg) throws Exception {
        String username = chatParticipator.getName();
        IChatSession iChatSession = chatParticipator.getChatSession();
        try {
            iChatSession.newMessage(msg, username);
            pushRetries = 0;
        } catch (RemoteException re) {
            if (pushRetries < MAXRETRIES) {
                Thread.sleep(1000);
                pushRetries++;
                pushMessage(chatParticipator, msg);
            } else {
                tryRecoverChat(chatParticipator);
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
    public static void notifyView(ChatNotificationType cnt, IMessage msg, IChatParticipator participator) throws Exception {
        if (cnt == ChatNotificationType.NEWMESSAGE) {
            System.out.println("not yet implemented");
        } else if (cnt == ChatNotificationType.USERJOINED || cnt == ChatNotificationType.USERLEFT) {
            System.out.println("not yet implemented");
        }
    }

    /**
     * This gets invoked when the client was not able to send a message after 5 retries (5 seconds)
     * It calls the server participator and verifies whether it's allowed to take over a session
     * After which it will notify other participators if it has taken over the session.
     * @param chatParticipator On which chatparticipator it should try to re-host the session
     * @throws Exception
     */
    private static void tryRecoverChat(ChatParticipator chatParticipator) throws Exception {
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
}
