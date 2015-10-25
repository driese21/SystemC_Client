package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.rmiInterfaces.*;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dries on 16/10/2015.
 */
public class Client extends UnicastRemoteObject {
    private static Client instance;
    private String username, domain, fullName;
    private int initiatorPort;
    private HashMap<IChatSession, IChatParticipator> sessions;
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
        sessions = new HashMap<>();
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
        this.clientSession.setChatInitiator(chatInitiator);
    }

    /**
     * This method gets called by the ClientSession and should be considered the 'other' client
     * @param other the chatsession we get invited too
     * @throws AlreadyBoundException
     * @throws RemoteException
     */
    public synchronized boolean startSession(IChatSession other) throws AlreadyBoundException, RemoteException {
        System.out.println("[INVITED CLIENT]Client");
        ChatParticipator chatParticipator = new ChatParticipator(username);
        chatParticipator.addChatSession(other);
        System.out.println("[INVITED CLIENT] Adding myself to participator list " + other.addParticipator(chatParticipator));
        sessions.put(other, chatParticipator);
        return true;
    }

    /**
     * This method gets used to start a chatsession with another user
     * @param username the other persons username
     * @throws ClientNotOnlineException
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    public void startSession(String username) throws ClientNotOnlineException, RemoteException, AlreadyBoundException {
        ChatParticipator chatParticipator = new ChatParticipator(this.username);
        ChatSession chs = new ChatSession(chatParticipator);
        sessions.put(chs, chatParticipator);
        chs.setChatName("MLG PARTY CHAT");
        if (clientSession.invite(username, chs)) {
            //if invite was successfull, add ourselves
            chatParticipator.addChatSession(chs);
        }
    }

    public void sendMessage(String msg) throws RemoteException, InterruptedException {
        Iterator it = sessions.entrySet().iterator();
        System.out.println("*** " + this.username + " ***");
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            IChatSession cs1 = (IChatSession)pair.getKey();
            ChatParticipator cp1 = (ChatParticipator)pair.getValue();
            System.out.println(cs1.getChatName());
            System.out.println(cp1.getName());
            cp1.pushMessage(msg);
        }

    }

    public String getUsername() {
        return username;
    }
}
