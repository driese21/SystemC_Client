package be.uantwerpen.interfaces;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatParticipator;

import java.rmi.RemoteException;

/**
 * Creator: Seb
 * Date: 8/11/2015
 */
public interface UIManagerInterface extends IAuthenticationManager, IChatManager, IClientManager {
    void openLogin();

    void openRegister();

    void openHome(String user);

    void openChat(String chatName) throws Exception;

    void openChat(ChatParticipator chatParticipator) throws RemoteException;
}
