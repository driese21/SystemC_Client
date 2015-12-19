package be.uantwerpen.interfaces.managers;

import be.uantwerpen.exceptions.ServerNotFoundException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 19/12/2015.
 */
public interface IMainManager {
    void chatServerFound(boolean found) throws RemoteException, MalformedURLException, NotBoundException;
}
