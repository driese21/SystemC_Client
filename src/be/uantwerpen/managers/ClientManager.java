package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatInitiator;
import be.uantwerpen.client.Client;
import be.uantwerpen.interfaces.IClientManager;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 3/11/2015.
 */
public class ClientManager implements IClientManager {

    public ClientManager() throws RemoteException {
        openPassive();
    }

    public void openPassive() throws RemoteException {
        ChatInitiator chatInitiator = new ChatInitiator();
        //Client.getInstance().getClientSession().setChatInitiator(chatInitiator);
    }

    @Override
    public ArrayList<String> getFriends() throws RemoteException {
        return Client.getInstance().getClientSession().getFriends();
    }

    @Override
    public boolean addFriend(String friendName) throws RemoteException {
        return Client.getInstance().getClientSession().addFriend(friendName);
    }

    @Override
    public boolean deleteFriend(String friendName) throws RemoteException {
        return Client.getInstance().getClientSession().deleteFriend(friendName);
    }


}
