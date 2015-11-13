package be.uantwerpen.managers;

import be.uantwerpen.connection.ConnectionLoader;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 10/11/2015.
 */
public class MainManager {
    private UIManager uiManager;
    private ClientManager clientManager;
    private ChatManager chatManager;
    private AuthenticationManager authenticationManager;

    public MainManager() throws RemoteException, MalformedURLException, NotBoundException {
        this.chatManager = new ChatManager();
        this.clientManager = new ClientManager(chatManager);
        this.authenticationManager = new AuthenticationManager(ConnectionLoader.getServer());
        this.uiManager = new UIManager(chatManager, clientManager, authenticationManager);
        //todo dit moet echt wel anders
        chatManager.setUiManagerInterface(this.uiManager);
    }
}
