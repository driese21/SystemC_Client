package be.uantwerpen.managers;

import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.interfaces.IAuthenticationManager;
import be.uantwerpen.rmiInterfaces.IClientAcceptor;
import be.uantwerpen.rmiInterfaces.IClientSession;

import java.rmi.RemoteException;

/**
 * Created by Dries on 3/11/2015.
 */
public class AuthenticationManager implements IAuthenticationManager {
    private IClientAcceptor clientAcceptor;

    public AuthenticationManager() { }

    public AuthenticationManager(IClientAcceptor clientAcceptor) {
        this.clientAcceptor = clientAcceptor;
    }

    @Override
    public boolean register(String username, String password, String fullName) {
        try {
            IClientSession ics = clientAcceptor.register(username,password,fullName);
            if (ics == null) {
                System.out.println("hier klopt iets niet");
                return false;
            }
            Client.getInstance(username,fullName,ics);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean login(String username, String password) throws InvalidCredentialsException {
        try {
            System.out.println(username);
            IClientSession ics = clientAcceptor.login(username, password);
            if (ics == null) {
                System.out.println("hier klopt ook iets niet");
                return false;
            }
            Client.getInstance(username,ics.getFullname(),ics);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
