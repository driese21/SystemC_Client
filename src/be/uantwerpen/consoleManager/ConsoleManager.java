package be.uantwerpen.consoleManager;

import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.rmiInterfaces.IChatServer;
import be.uantwerpen.rmiInterfaces.IClientSession;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Dries on 17/10/2015.
 */
public class ConsoleManager {
    private Client client;
    IChatServer iChatServer;
    private String username, ip;

    public ConsoleManager(String username, String ip) throws AlreadyBoundException, IOException, InvalidCredentialsException, ClientNotOnlineException, InterruptedException {
        this.username = username;
        this.ip = ip;
        this.client = Client.getInstance();
        initiateConnection();
        interact();
    }

    private void initiateConnection() {
        try {
            iChatServer = (IChatServer) Naming.lookup("//" + ip + ":11337" + "/ChatServer");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void interact() throws IOException, AlreadyBoundException, InvalidCredentialsException, ClientNotOnlineException, InterruptedException {
        IClientSession ics = iChatServer.register(username +"@1337.com", "hello", "Dries Eestermans");
        client = Client.getInstance(username +"@1337.com", "Dries Eestermans", ics);
        if (username.equalsIgnoreCase("dries")) {
            client.invite("jef");
            try {
                client.sendMessage("hoe gaat ie joh");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (username.equalsIgnoreCase("jef")) {
            Thread.sleep(10000);
            try {
                client.sendMessage("test recovery");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
