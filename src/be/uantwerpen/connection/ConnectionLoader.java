package be.uantwerpen.connection;

import be.uantwerpen.rmiInterfaces.IServerListener;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 */
public class ConnectionLoader {
    public static IServerListener getServer() throws RemoteException, NotBoundException, MalformedURLException {
        return (IServerListener) Naming.lookup("//" + "109.128.0.13:11337" + "/ChatServer");
    }
}
