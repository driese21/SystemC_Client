package be.uantwerpen.client;

import be.uantwerpen.enums.ClientStatusType;
import be.uantwerpen.interfaces.IChatManager;
import be.uantwerpen.interfaces.IClientManager;
import be.uantwerpen.rmiInterfaces.IClientListener;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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

    @Override
    public void friendListUpdated() throws RemoteException {
        clientManager.friendListUpdated();
    }
}
