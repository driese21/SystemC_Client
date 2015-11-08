package be.uantwerpen.chat;

import be.uantwerpen.client.Client;
import be.uantwerpen.managers.ChatManager;
import be.uantwerpen.rmiInterfaces.IChatInitiator;
import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Dries on 16/10/2015.
 */
public class ChatInitiator extends UnicastRemoteObject implements IChatInitiator {

    public ChatInitiator() throws RemoteException{
    }

    @Override
    public boolean initialHandshake(IChatSession otherChatSession) throws RemoteException {
        System.out.println("[INVITED CLIENT]ChatInitiator");
        //return Client.getInstance().invite(otherChatSession);
        return ChatManager.invite(otherChatSession);
    }


}
