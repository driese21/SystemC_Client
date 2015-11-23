package be.uantwerpen.managers;

import be.uantwerpen.client.ClientListener;
import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.interfaces.managers.IClientManager;
import be.uantwerpen.interfaces.managers.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 3/11/2015.
 */
public class ClientManager implements IClientManager {
    private Client client;
    private IChatManager chatManager;
    private UIManagerInterface uiManagerInterface;

    public ClientManager(IChatManager chatManager, Client client) throws RemoteException {
        this.client = client;
        this.chatManager = chatManager;
    }

    public void openPassive() throws RemoteException {
        ClientListener clientListener = new ClientListener(chatManager, this);
        client.getClientSession().setClientListener(clientListener);
    }

    public void setUiManagerInterface(UIManagerInterface uiManagerInterface) {
        this.uiManagerInterface = uiManagerInterface;
    }

    @Override
    public ArrayList<String> getFriends() throws RemoteException {
        return client.getClientSession().getFriends();
    }

    @Override
    public boolean addFriend(String friendName) throws RemoteException, UnknownClientException {
        return client.getClientSession().addFriend(friendName);
    }

    @Override
    public boolean deleteFriend(String friendName) throws RemoteException {
        return client.getClientSession().deleteFriend(friendName);
    }

    @Override
    public void friendListUpdated() throws RemoteException {
        uiManagerInterface.updateFriendList(getFriends());
    }

    @Override
    public ArrayList<IChatSession> getOfflineMessages() throws RemoteException {
        return client.getClientSession().getOfflineMessage();
    }

    @Override
    public void offlineMessagesRead() throws RemoteException {
        client.getClientSession().offlineMessagesRead();
    }
}
