package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.rmiInterfaces.*;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

/**
 * Created by Dries on 16/10/2015.
 */
public class Client extends UnicastRemoteObject {
    private static Client instance;
    private String username, domain, fullName;
    private HashSet<ChatParticipator> sessions;
    //private ChatInitiator chatInitiator;
    private IClientSession clientSession;

    public static Client getInstance() throws RemoteException {
        if (instance == null) instance = new Client();
        return instance;
    }

    public static Client getInstance(String username, String fullName, IClientSession clientSession) throws RemoteException {
        if (instance == null || instance.username == null) instance = new Client(username, fullName, clientSession);
        return instance;
    }

    private Client() throws RemoteException {
        sessions = new HashSet<>();
    }

    private Client(String username, String fullName, IClientSession clientSession) throws RemoteException {
        this();
        this.username = username.split("@")[0];
        this.domain = username.split("@")[1];
        this.fullName = fullName;
        this.clientSession = clientSession;
        System.out.println(this.clientSession.getUsername());
        //openPassive();
    }



    /**
     * This method gets called by the ClientSession and should be considered the 'other' client
     * @param other the chatsession we get invited too
     * @throws AlreadyBoundException
     * @throws RemoteException
    public synchronized boolean invite(IChatSession other) throws RemoteException {
        System.out.println("[INVITED CLIENT]Client");
        ChatParticipator chatParticipator = new ChatParticipator(username);
        chatParticipator.addChatSession(other);
        System.out.println("[INVITED CLIENT] Adding myself to participator list " + other.joinSession(chatParticipator, false));
        sessions.add(chatParticipator);
        return true;
    }*/

    /**
     * This method gets used to start a chatsession with another user
     * @param username the other persons username
     * @throws ClientNotOnlineException
     * @throws RemoteException
     * @throws AlreadyBoundException
    public void invite(String username) throws ClientNotOnlineException, RemoteException {
        ChatParticipator chatParticipator = new ChatParticipator(this.username);
        ChatSession chs = new ChatSession(chatParticipator);
        chs.setChatName(Client.getInstance().fullName);
        chatParticipator.addChatSession(chs);
        if (clientSession.invite(username, chs)) {
            //if invite was successfull, remember it, otherwise garbage
            sessions.add(chatParticipator);
        }
    }*/

    /*public void sendMessage(String msg) throws Exception {
        for (ChatParticipator participator : sessions) {
            participator.pushMessage(msg);
        }
        *//*Iterator it = sessions.entrySet().iterator();
        System.out.println("["+this.username+"]Sending message");
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            IChatSession cs1 = (IChatSession)pair.getKey();
            ChatParticipator cp1 = (ChatParticipator)pair.getValue();
            //System.out.println(cs1.getChatName());
            //System.out.println(cp1.getName());
            cp1.pushMessage(msg);
        }*//*
    }*/

    public String getUsername() {
        return username;
    }

    public IClientSession getClientSession() {
        return clientSession;
    }

    public void addSession(ChatParticipator participator) {
        sessions.add(participator);
    }
}
