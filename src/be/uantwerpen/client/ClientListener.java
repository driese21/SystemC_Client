package be.uantwerpen.client;

import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.interfaces.managers.IClientManager;
import be.uantwerpen.rmiInterfaces.IClientListener;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public class ClientListener extends UnicastRemoteObject implements IClientListener {
    private IChatManager chatManager;
    private IClientManager clientManager;

    public ClientListener() throws RemoteException{
    }

    public ClientListener(IChatManager chatManager, IClientManager clientManager) throws RemoteException {
        this.chatManager = chatManager;
        this.clientManager = clientManager;
    }

    /**
     * Listens for incoming invites and passes them to the ChatManager to do further crunching
     * @param otherChatSession The ChatSession it got invited too
     * @return true if handshake and invite was successful
     * @throws RemoteException
     */
    @Override
    public boolean initialHandshake(IChatSession otherChatSession) throws RemoteException {
        System.out.println("I received an invite...");
        return chatManager.invite(otherChatSession);
    }

    /**
     * Used to check if client is still alive
     * @return True if the client is still alive
     * @throws RemoteException When client is no longer online
     */
    @Override
    public boolean alive() throws RemoteException {
        return true;
    }


    /**
     * Updates the friendlist if you add or delete a friend
     * @throws RemoteException
     */
    @Override
    public void friendListUpdated() throws RemoteException {
        clientManager.friendListUpdated();
    }

    @Override
    public void friendOnline(String friendName, IClientListener friendListener, boolean ack) throws RemoteException {
        clientManager.friendOnline(friendName, friendListener, ack);
    }
}
