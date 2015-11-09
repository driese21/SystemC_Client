package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.exceptions.InvalidCredentialsException;
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
    private IClientSession clientSession;

    public static Client getInstance() throws RemoteException {
        if (instance == null) instance = new Client();
        return instance;
    }

    public static Client getInstance(String username, String fullName, IClientSession clientSession) throws RemoteException, InvalidCredentialsException {
        if (instance == null || instance.username == null) instance = new Client(username, fullName, clientSession);
        return instance;
    }

    private Client() throws RemoteException {
        sessions = new HashSet<>();
    }

    private Client(String username, String fullName, IClientSession clientSession) throws RemoteException, InvalidCredentialsException {
        this();
        trySplitUsername(username);
        this.fullName = fullName;
        this.clientSession = clientSession;
        System.out.println(this.clientSession.getUsername());
    }

    private void trySplitUsername(String username) throws InvalidCredentialsException {
        try {
            this.username = username.split("@")[0];
            this.domain = username.split("@")[1];
        } catch (ArrayIndexOutOfBoundsException aiooe) {
            throw new InvalidCredentialsException("Wrong username or domain name");
        }
    }

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
