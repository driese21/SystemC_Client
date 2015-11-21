package be.uantwerpen.interfaces.managers;

import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 3/11/2015.
 */
public interface IClientManager {
    void openPassive() throws RemoteException;

    ArrayList<String> getFriends() throws RemoteException;

    boolean addFriend(String friendName) throws RemoteException, UnknownClientException;

    boolean deleteFriend(String friendName) throws RemoteException;

    void friendListUpdated() throws RemoteException;

    ArrayList<IChatSession> getOfflineMessages() throws RemoteException;
}
