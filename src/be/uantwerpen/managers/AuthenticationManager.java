package be.uantwerpen.managers;

import be.uantwerpen.client.Client;
import be.uantwerpen.client.StatusUpdater;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.interfaces.managers.IAuthenticationManager;
import be.uantwerpen.interfaces.managers.IClientManager;
import be.uantwerpen.rmiInterfaces.IServerListener;
import be.uantwerpen.rmiInterfaces.IClientSession;

import java.rmi.RemoteException;

/**
 * Created by Dries on 3/11/2015.
 */
public class AuthenticationManager implements IAuthenticationManager {
    private Client client;
    private IClientManager clientManager;
    private IServerListener serverListener;

    public AuthenticationManager() { }

    public AuthenticationManager(IServerListener serverListener, Client client, IClientManager clientManager) {
        this.serverListener = serverListener;
        this.clientManager = clientManager;
        this.client = client;
    }

    @Override
    public boolean register(String username, String password, String fullName) {
        try {
            IClientSession ics = serverListener.register(username,password,fullName);

            if (ics == null) {
                System.out.println("hier klopt iets niet");
                return false;
            }
            client.setFullName(fullName);
            client.setUsername(username);
            client.setClientSession(ics);
            //client = new Client(username,fullName,ics);
            finalizeAuthentication(ics);
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
            IClientSession ics = serverListener.login(username, password);
            if (ics == null) {
                System.out.println("hier klopt ook iets niet");
                return false;
            }
            //client = new Client(username,ics.getFullname(),ics);
            client.setUsername(username);
            client.setFullName(ics.getFullname());
            client.setClientSession(ics);
            finalizeAuthentication(ics);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void finalizeAuthentication(IClientSession ics) throws RemoteException {
        clientManager.openPassive();
        new Thread(new StatusUpdater(ics)).start();
    }
}
