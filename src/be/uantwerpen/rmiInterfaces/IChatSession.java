package be.uantwerpen.rmiInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Dries on 16/10/2015.
 */
public interface IChatSession extends Remote {
    boolean newMessage(String msg,String username) throws RemoteException;
}
