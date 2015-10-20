package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.rmiInterfaces.IClientSession;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Dries on 16/10/2015.
 */
public class Client extends UnicastRemoteObject {
    private static Client instance;
    private String username, password, IP;
    private int initiatorPort;
    private ArrayList<ChatSession> sessions;
    private ChatInitiator chatInitiator;
    private IClientSession clientSession;

    public static Client getInstance() throws RemoteException, AlreadyBoundException {
        if (instance == null) instance = new Client();
        return instance;
    }

    public static Client getInstance(String username, String password, String IP) throws RemoteException, AlreadyBoundException {
        if (instance == null || instance.username == null) instance = new Client(username, password, IP);
        return instance;
    }

    private Client() throws RemoteException, AlreadyBoundException {
        sessions = new ArrayList<>();
        openPassive();
    }

    private Client(String username, String password, String IP) throws RemoteException, AlreadyBoundException {
        this();
        this.username = username;
        this.password = password;
        this.IP = IP;
        this.initiatorPort = 11338;
    }

    public void openPassive() throws RemoteException, AlreadyBoundException {
        chatInitiator = new ChatInitiator(initiatorPort);
        Registry registry = LocateRegistry.createRegistry(chatInitiator.getPort());
        try {
            registry.bind("ChatInitiator", chatInitiator);
        } catch (AlreadyBoundException abe) {
            if (initiatorPort < 11350) { //try again if smaller than 11350
                initiatorPort++;
                openPassive();
            }
            else throw abe; //something seriously wrong
        }
    }

    public ChatSession startSession(ChatSession other) {
        Chat chat = new Chat();
        ChatSession cs = new ChatSession(other, chat);
        sessions.add(cs);
        return cs;
    }

    public void startSession(String username) {

        ChatSession chatSession = new ChatSession(); //start empty session
    }

    public void addClientSession(int port) throws NotBoundException, MalformedURLException, RemoteException {
        IClientSession clientSession = (IClientSession) Naming.lookup("//" + "127.0.0.1:" + port + "/ClientSession");
        this.clientSession = clientSession;
        clientSession.getOtherUsers().forEach(o -> System.out.println(o));
    }

    public ArrayList<ChatSession> getSessions() {
        return sessions;
    }

    public String getUsername() {
        return username;
    }
}
