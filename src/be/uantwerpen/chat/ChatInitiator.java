package be.uantwerpen.chat;

import be.uantwerpen.client.Client;
import be.uantwerpen.rmiInterfaces.IChatInitiator;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 16/10/2015.
 */
public class ChatInitiator extends Thread implements IChatInitiator {
    private int port;

    public ChatInitiator(int port) {
        this.port = port;
    }

    @Override
    public ChatSession initialHandshake(ChatSession otherChatSession) throws RemoteException, AlreadyBoundException {
        return Client.getInstance().startSession(otherChatSession);
    }

    public int getPort() {
        return port;
    }
}
