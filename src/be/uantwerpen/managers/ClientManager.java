package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatInitiator;
import be.uantwerpen.client.Client;
import be.uantwerpen.interfaces.IChatManager;
import be.uantwerpen.interfaces.IClientManager;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 3/11/2015.
 */
public class ClientManager implements IClientManager {

    public ClientManager(IChatManager chatManager) throws RemoteException {
        openPassive(chatManager);
    }

    public void openPassive(IChatManager chatManager) throws RemoteException {
        ChatInitiator chatInitiator = new ChatInitiator(chatManager);
        Client.getInstance().getClientSession().setChatInitiator(chatInitiator);
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
