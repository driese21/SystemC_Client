package be.uantwerpen;

import be.uantwerpen.managers.MainManager;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 16/10/2015.
 */
public class ClientChat {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        MainManager mainManager = new MainManager();
    }
}
