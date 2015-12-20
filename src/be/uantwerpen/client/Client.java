package be.uantwerpen.client;

import be.uantwerpen.chat.*;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.rmiInterfaces.*;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Dries on 16/10/2015.
 */
public class Client {
    private String username;
    private HashSet<ChatParticipator> sessions;
    private IClientSession clientSession;
    private HashMap<ClientKey, IClientListener> friends;

    public Client() {
        this.sessions = new HashSet<>();
        this.friends = new HashMap<>();
    }

    public Client(String username, IClientSession clientSession) throws InvalidCredentialsException, RemoteException {
        this();
        this.username = username;
        this.clientSession = clientSession;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public HashMap<ClientKey, IClientListener> getFriends() {
        return friends;
    }

    public void addSession(ChatParticipator participator) {
        sessions.add(participator);
    }

    public void friendOnline(ClientKey clientKey, IClientListener friendListener) {
        friends.put(clientKey, friendListener);
    }

    public IClientListener getFriendListener(ClientKey clientKey) {
        return friends.get(clientKey);
    }
}
