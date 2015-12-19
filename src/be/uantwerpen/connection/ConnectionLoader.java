package be.uantwerpen.connection;

import be.uantwerpen.exceptions.ServerNotFoundException;
import be.uantwerpen.rmiInterfaces.IServerListener;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 */
public class ConnectionLoader {
    public static IServerListener getServer(InetAddress serverIp) throws RemoteException, NotBoundException, MalformedURLException {
        return (IServerListener) Naming.lookup("//" + serverIp.getHostAddress() + ":11337" + "/ChatServer");
    }
}
