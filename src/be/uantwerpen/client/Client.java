package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.rmiInterfaces.*;

import java.rmi.RemoteException;
import java.util.HashSet;

/**
 * Created by Dries on 16/10/2015.
 */
public class Client {
    private String username,  fullName;
    private HashSet<ChatParticipator> sessions;
    private IClientSession clientSession;

    public Client() {
        sessions = new HashSet<>();
    }

    public Client(String username, String fullName, IClientSession clientSession) throws InvalidCredentialsException, RemoteException {
        this();
        this.username = username;
        this.fullName = fullName;
        this.clientSession = clientSession;
        System.out.println(this.clientSession.getUsername());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setClientSession(IClientSession clientSession) {
        this.clientSession = clientSession;
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
