package be.uantwerpen.managers;

import be.uantwerpen.client.ClientKey;
import be.uantwerpen.client.ClientListener;
import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.interfaces.managers.IClientManager;
import be.uantwerpen.interfaces.managers.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IClientListener;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Dries on 3/11/2015.
 */
public class ClientManager implements IClientManager {
    private Client client;
    private ClientListener clientListener;
    private IChatManager chatManager;
    private UIManagerInterface uiManagerInterface;

    public ClientManager(IChatManager chatManager, Client client) throws RemoteException {
        this.client = client;
        this.chatManager = chatManager;
    }

    public void openPassive() throws RemoteException {
        this.clientListener = new ClientListener(chatManager, this);
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

    /**
     * Received notification that one of my friends came online, update their listener
     * @param friendName The name of this friend
     * @param friendListener Their ClientListener
     */
    @Override
    public void friendOnline(String friendName, IClientListener friendListener, boolean ack) throws RemoteException {
        System.out.println(friendName + " just came online!");
        client.friendOnline(new ClientKey(friendName), friendListener);
        checkFriendsStatus();
        if (!ack) {
            friendListener.friendOnline(client.getClientSession().getUsername(), clientListener, true);
        }
    }

    /**
     * Check status of all friends, if they're unreachable, set their listener to null
     */
    private void checkFriendsStatus() {
        for (Object o : client.getFriends().entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            ClientKey ck = (ClientKey) pair.getKey();
            IClientListener listener = (IClientListener) pair.getValue();
            try {
                if (listener != null && listener.alive()) { }
            } catch (RemoteException e) {
                client.friendOnline(ck, null);
                System.out.println("Can't reach " + ck.getUsername());
            }
        }
    }
}
