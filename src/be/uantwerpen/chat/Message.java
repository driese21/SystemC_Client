package be.uantwerpen.chat;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

/**
 * Created by Dries on 17/10/2015.
 */
public class Message extends UnicastRemoteObject {
    private String message, username;

    public Message() throws RemoteException {
    }

    public Message(String message, String username) throws RemoteException {
        this.message = message;
        this.username = username;
    }

    public Message (Message message) throws RemoteException {
        this.message = message.message;
        this.username = message.username;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
