package be.uantwerpen.rmiInterfaces;

import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.InvalidCredentialsException;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public interface IChatServer extends Remote {
    IClientSession register(String username, String password, String fullName) throws RemoteException, AlreadyBoundException, InvalidCredentialsException;
    IClientSession login(String username, String password) throws RemoteException, AlreadyBoundException, InvalidCredentialsException;
}
