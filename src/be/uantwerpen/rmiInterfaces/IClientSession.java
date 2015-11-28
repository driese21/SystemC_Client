package be.uantwerpen.rmiInterfaces;

import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.UnknownClientException;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public interface IClientSession extends Remote {
    boolean updateStatus() throws RemoteException;
    boolean addFriend(String friendName) throws RemoteException, UnknownClientException;
    ArrayList<String> getFriends() throws RemoteException;
    boolean deleteFriend(String friendName) throws RemoteException;
    IChatSession sendInvite(String otherUsername, IChatSession ics) throws RemoteException, UnknownClientException;
    boolean invite(IChatSession ics) throws AlreadyBoundException, RemoteException;
    void setClientListener(IClientListener ici) throws RemoteException;
    String getUsername() throws RemoteException;
    String getFullName() throws RemoteException;
    ArrayList<IChatSession> getOfflineMessage() throws RemoteException;
    void offlineMessagesRead() throws RemoteException;
}

