package be.uantwerpen.consoleManager;

import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.rmiInterfaces.IChatServer;
import be.uantwerpen.rmiInterfaces.IClientSession;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Dries on 17/10/2015.
 */
public class ConsoleManager {
    private Client client;
    IChatServer iChatServer;

    public ConsoleManager() throws AlreadyBoundException, IOException, InvalidCredentialsException {
        this.client = Client.getInstance();
        initiateConnection();
        interact();
    }

    private void initiateConnection() {
        try {
            iChatServer = (IChatServer) Naming.lookup("//" + "127.0.0.1:11337" + "/ChatServer");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void interact() throws IOException, AlreadyBoundException, InvalidCredentialsException {
        IClientSession ics = iChatServer.register("Dries", "hello");

    }
}
