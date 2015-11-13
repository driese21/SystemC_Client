package be.uantwerpen.managers;

import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.interfaces.IAuthenticationManager;
import be.uantwerpen.interfaces.IChatManager;
import be.uantwerpen.interfaces.IClientManager;
import be.uantwerpen.rmiInterfaces.IClientAcceptor;
import be.uantwerpen.rmiInterfaces.IClientSession;

import java.rmi.RemoteException;

/**
 * Created by Dries on 3/11/2015.
 */
public class AuthenticationManager implements IAuthenticationManager {
    private Client client;
    private IClientManager clientManager;
    private IChatManager chatManager;
    private IClientAcceptor clientAcceptor;

    public AuthenticationManager() { }

    public AuthenticationManager(IClientAcceptor clientAcceptor, Client client, IClientManager clientManager, IChatManager chatManager) {
        this.clientAcceptor = clientAcceptor;
        this.clientManager = clientManager;
        this.chatManager = chatManager;
        this.client = client;
    }

    @Override
    public boolean register(String username, String password, String fullName) {
        try {
            IClientSession ics = clientAcceptor.register(username,password,fullName);
            if (ics == null) {
                System.out.println("hier klopt iets niet");
                return false;
            }
            client.setFullName(fullName);
            client.setUsername(username);
            client.setClientSession(ics);
            //client = new Client(username,fullName,ics);
            notifyClientMgr();
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
            //client = new Client(username,ics.getFullname(),ics);
            client.setUsername(username);
            client.setFullName(ics.getFullname());
            client.setClientSession(ics);
            notifyClientMgr();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void notifyClientMgr() throws RemoteException {
        clientManager.openPassive(chatManager);
    }
}
