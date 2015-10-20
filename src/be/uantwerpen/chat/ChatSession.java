package be.uantwerpen.chat;

import be.uantwerpen.rmiInterfaces.IChatSession;

import java.rmi.RemoteException;

/**
 * Created by Dries on 16/10/2015.
 */
public class ChatSession extends Thread implements IChatSession {
    private Chat chat;
    private ChatSession other;

    public ChatSession() {

    }

    public ChatSession(ChatSession other, Chat chat) {
        this.chat = chat;
        this.other = other;
    }

    @Override
    public boolean newMessage(String msg,String username) throws RemoteException {
        System.out.println(msg);
        return false;
    }
}
