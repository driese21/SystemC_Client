package be.uantwerpen.rmiInterfaces;

import be.uantwerpen.chat.Message;
import be.uantwerpen.enums.ChatNotificationType;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Dries on 23/10/2015.
 */
public interface IChatParticipator extends Remote {
    void notifyListener(ChatNotificationType cnt, IMessage msg) throws RemoteException;
    void notifyListener(ChatNotificationType cnt, IChatParticipator newParticipator) throws RemoteException;
    void addChatSession(IChatSession chatSession) throws RemoteException;
    String getName() throws RemoteException;
    void pushMessage(String msg) throws Exception;
    boolean isServer() throws RemoteException;
    boolean hostChat(IChatParticipator newHost) throws RemoteException;
    boolean alive() throws RemoteException;
    void cloneSession(ChatNotificationType cnt) throws RemoteException;
    void hostChanged(IChatParticipator newHost, IChatSession newSession) throws RemoteException;
}
