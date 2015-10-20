package be.uantwerpen.rmiInterfaces;

import be.uantwerpen.chat.ChatSession;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Dries on 16/10/2015.
 */
public interface IChatInitiator extends Remote {
    ChatSession initialHandshake(ChatSession otherChatSession) throws RemoteException, AlreadyBoundException;
}
