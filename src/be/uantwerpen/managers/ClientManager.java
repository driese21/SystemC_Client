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
    private Client client;
    private IChatManager chatManager;

    public ClientManager(IChatManager chatManager, Client client) throws RemoteException {
        this.client = client;
        this.chatManager = chatManager;
        //openPassive(chatManager);
    }

    public void openPassive(IChatManager chatManager) throws RemoteException {
        ChatInitiator chatInitiator = new ChatInitiator(chatManager);
        client.getClientSession().setChatInitiator(chatInitiator);
    }

    @Override
    public ArrayList<String> getFriends() throws RemoteException {
        return client.getClientSession().getFriends();
    }

    @Override
    public boolean addFriend(String friendName) throws RemoteException {
        return client.getClientSession().addFriend(friendName);
    }

    @Override
    public boolean deleteFriend(String friendName) throws RemoteException {
        return client.getClientSession().deleteFriend(friendName);
    }


}
