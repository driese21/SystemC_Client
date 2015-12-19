package be.uantwerpen.managers;

import be.uantwerpen.client.Client;
import be.uantwerpen.connection.ConnectionLoader;
import be.uantwerpen.exceptions.ServerNotFoundException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 */
public class MainManager {
    private Client client;
    private UIManager uiManager;
    private ClientManager clientManager;
    private ChatManager chatManager;
    private AuthenticationManager authenticationManager;
    private DiscoveryManager discoveryManager;

    public MainManager() throws RemoteException, MalformedURLException, NotBoundException {
        this.discoveryManager = new DiscoveryManager();
        this.client = new Client();
        this.chatManager = new ChatManager(client);
        this.clientManager = new ClientManager(chatManager, client);
        try {
            this.authenticationManager = new AuthenticationManager(ConnectionLoader.getServer(discoveryManager.getServerIp()), client, clientManager);
        } catch (ServerNotFoundException e) {
            e.printStackTrace();
            return;
        }
        this.uiManager = new UIManager(chatManager, clientManager, authenticationManager);
        this.chatManager.setUiManagerInterface(this.uiManager);
        this.clientManager.setUiManagerInterface(this.uiManager);
    }
}
