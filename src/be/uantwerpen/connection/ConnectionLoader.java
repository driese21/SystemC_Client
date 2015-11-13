package be.uantwerpen.connection;

import be.uantwerpen.rmiInterfaces.IClientAcceptor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 */
public class ConnectionLoader {
    public static IClientAcceptor getServer() throws RemoteException, NotBoundException, MalformedURLException {
        return (IClientAcceptor) Naming.lookup("//" + "127.0.0.1:11337" + "/ChatServer");
    }
}
