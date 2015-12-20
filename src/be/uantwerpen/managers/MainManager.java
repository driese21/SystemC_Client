package be.uantwerpen.managers;

import be.uantwerpen.client.Client;
import be.uantwerpen.connection.ConnectionLoader;
import be.uantwerpen.exceptions.ServerNotFoundException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 * Manager Manager
 * Manages and starts all other managers
 */
public class MainManager implements be.uantwerpen.interfaces.managers.IMainManager {
    private Client client;
    private UIManager uiManager;
    private ClientManager clientManager;
    private ChatManager chatManager;
    private AuthenticationManager authenticationManager;
    private DiscoveryManager discoveryManager;

    public MainManager() throws NotBoundException {
        this.discoveryManager = new DiscoveryManager(this);
        new Thread(discoveryManager).start();
    }

    @Override
    public void chatServerFound(boolean found) throws RemoteException, MalformedURLException, NotBoundException {
        if (found) {
            this.client = new Client();
            this.chatManager = new ChatManager(client);
            this.clientManager = new ClientManager(chatManager, client);
            this.authenticationManager = new AuthenticationManager(ConnectionLoader.getServer(discoveryManager.getServerIp()), client, clientManager);
            this.uiManager = new UIManager(chatManager, clientManager, authenticationManager);
            this.chatManager.setUiManagerInterface(this.uiManager);
            this.clientManager.setUiManagerInterface(this.uiManager);
        } else try {
            throw new ServerNotFoundException("Server not found on network");
        } catch (ServerNotFoundException e) {
            e.printStackTrace();
        }
    }
}
