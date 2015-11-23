package be.uantwerpen.interfaces.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Creator: Seb
 * Date: 8/11/2015
 */
public interface UIManagerInterface extends IAuthenticationManager, IChatManager, IClientManager {
    void openLogin();

    void openRegister();

    void openHome(String user) throws RemoteException;

    void openChat(ChatParticipator chatParticipator) throws RemoteException;

    void openChat(IChatSession offlineSession) throws RemoteException;

    void updateFriendList(ArrayList<String> friends);

    ArrayList<ChatParticipator> getActiveChatSessions();

    ArrayList<String> getMessages(IChatSession ics) throws RemoteException;
}
