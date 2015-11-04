package be.uantwerpen.interfaces;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 3/11/2015.
 */
public interface IClientManager {
    ArrayList<String> getFriends() throws RemoteException;

    boolean addFriend(String friendName) throws RemoteException;

    boolean deleteFriend(String friendName) throws RemoteException;
}
