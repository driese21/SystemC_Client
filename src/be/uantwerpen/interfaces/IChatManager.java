package be.uantwerpen.interfaces;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 */
public interface IChatManager {
    ChatParticipator sendInvite(String friendName) throws RemoteException, ClientNotOnlineException;

    boolean invite(IChatSession chatSession) throws RemoteException;

    void pushMessage(ChatParticipator chatParticipator, String msg) throws Exception;

    void notifyView(ChatNotificationType cnt, IMessage msg, ChatParticipator participator);
}
