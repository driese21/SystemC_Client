package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.rmiInterfaces.*;

import java.rmi.AlreadyBoundException;
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
    private String username, domain, fullName;
    private int initiatorPort;
    private ArrayList<IChatSession> sessions;
    private ChatInitiator chatInitiator;
    private IClientSession clientSession;

    public static Client getInstance() throws RemoteException, AlreadyBoundException {
        if (instance == null) instance = new Client();
        return instance;
    }

    public static Client getInstance(String username, String fullName, IClientSession clientSession) throws RemoteException, AlreadyBoundException {
        if (instance == null || instance.username == null) instance = new Client(username, fullName, clientSession);
        return instance;
    }

    private Client() throws RemoteException, AlreadyBoundException {
        sessions = new ArrayList<>();
    }

    private Client(String username, String fullName, IClientSession clientSession) throws RemoteException, AlreadyBoundException {
        this();
        this.username = username.split("@")[0];
        this.domain = username.split("@")[1];
        this.fullName = fullName;
        this.clientSession = clientSession;
        this.initiatorPort = 11339;
        System.out.println(this.clientSession.getUsername());
        openPassive();
    }

    public void openPassive() throws RemoteException, AlreadyBoundException {
        chatInitiator = new ChatInitiator(initiatorPort);
        /*try {
            Registry registry = LocateRegistry.createRegistry(initiatorPort);
            registry.bind("ChatInitiator", chatInitiator);
        } catch (AlreadyBoundException abe) {
            if (initiatorPort < 11350) { //try again if smaller than 11350
                initiatorPort++;
                openPassive();
            } else throw abe; //something seriously wrong
        }*/
        this.clientSession.setChatInitiator(chatInitiator);
    }

    public void startSession(IChatSession other) throws AlreadyBoundException, RemoteException {
        //Chat chat = new Chat();
        sessions.add(other);
        System.out.println("added new chatsession with other");
        //ChatSession cs = new ChatSession(other, chat);
        //sessions.add(cs);
    }

    public void startSession(String username) throws ClientNotOnlineException, RemoteException, AlreadyBoundException {
        ChatSession chs = new ChatSession();
        chs.setChat(new Chat());
        clientSession.startChatSession(username, chs);
    }

    public void sendMessage(String msg) throws RemoteException, InterruptedException {
        Message message = new Message(msg, username);
        System.out.println(message.toString());
        sessions.get(0).newMessage(message);
    }

    public String getUsername() {
        return username;
    }
}
