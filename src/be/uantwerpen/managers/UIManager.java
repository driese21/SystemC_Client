package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.enums.ClientStatusType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.guiChatC.ChatPage;
import be.uantwerpen.guiChatC.HomePage;
import be.uantwerpen.guiChatC.Login;
import be.uantwerpen.guiChatC.Register;
import be.uantwerpen.interfaces.managers.IAuthenticationManager;
import be.uantwerpen.interfaces.managers.IChatManager;
import be.uantwerpen.interfaces.managers.IClientManager;
import be.uantwerpen.interfaces.managers.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Creator: Seb
 * Date: 8/11/2015
 * Manages the UI of all the pages
 */
public class UIManager implements UIManagerInterface {
    private HashMap<ChatParticipator, ChatPage> chatPageHashMap;
    private HashMap<String, ClientStatusType> friends;
    private HomePage homePage;
    private IChatManager chatManager;
    private IClientManager clientManager;
    private IAuthenticationManager authenticationManager;

    public UIManager(IChatManager chatManager, IClientManager clientManager, IAuthenticationManager authenticationManager) {
        this.chatPageHashMap = new HashMap<>();
        this.friends = new HashMap<>();
        this.chatManager = chatManager;
        this.clientManager = clientManager;
        this.authenticationManager = authenticationManager;
        openLogin();
    }

    public void openLogin(){
        Login login = new Login(this);
    }

    public void openRegister(){
        Register registerForm = new Register(this);
    }

    public void openHome(String user) throws RemoteException {
        this.homePage = new HomePage(this, user);
        ArrayList<IChatSession> offlineSessions = getOfflineMessages();
        updateFriendList(getFriends());
        if (offlineSessions == null) {
            return;
        }
        for (IChatSession iChatSession : offlineSessions) {
            openChat(iChatSession);
        }
    }

    /**
     * Open ChatPage, either from GUI or because we received an invitation
     * @param chatParticipator Reference to a ChatParticipator created by ChatManager
     * @throws RemoteException
     */
    @Override
    public void openChat(ChatParticipator chatParticipator) throws RemoteException {
        if (chatParticipator == null) return;
        ChatPage chatPage = chatPageHashMap.get(chatParticipator);
        if (chatPage != null) {
            System.out.println("This ChatPage exists, let's set it visible");
            chatPage.setVisible(true);
            chatPage.toFront();
        } else {
            System.out.println("This ChatPage does not exist yet, creating new one");
            chatPage = new ChatPage(chatParticipator.getChatName(), this, chatParticipator);
            chatPageHashMap.put(chatParticipator, chatPage);
        }
        homePage.updateChats();
    }

    /**
     * Opens a chat windows per offline ChatSession we received
     * @param offlineSession A reference to the server object
     * @throws RemoteException
     */
    @Override
    public void openChat(IChatSession offlineSession) throws RemoteException {
        ChatPage offlineChat = new ChatPage(offlineSession.getChatName(), this, offlineSession);
    }


    @Override
    public ArrayList<String> getMessages(IChatSession ics) throws RemoteException {
        ArrayList<String> messages = new ArrayList<>();
        StringBuilder sb;
        for (IMessage message : ics.getMessages()) {
            sb = new StringBuilder();
            sb.append("[");
            sb.append(message.getDate());
            sb.append("]");
            sb.append(" ");
            sb.append(message.getUsername());
            sb.append(": ");
            sb.append(message.getMessage());
            messages.add(sb.toString());
        }
        return messages;
    }

    @Override
    public ArrayList<ChatParticipator> getActiveChatSessions() {
        return new ArrayList<>(chatPageHashMap.keySet());
    }

    /**
     * Updates the friendlist on the homePage
     * @param friends name of the friends
     */
    @Override
    public void updateFriendList(ArrayList<String> friends) {
        homePage.updateFriendList(friends);
    }

    //region AuthenticationManager
    @Override
    public boolean register(String username, String password, String fullName) throws RemoteException {
        if (authenticationManager.register(username, password, fullName)) {
            openHome(username);
            return true;
        } return false;
    }

    @Override
    public boolean login(String username, String password) throws InvalidCredentialsException, RemoteException {
        if (authenticationManager.login(username,password)) {
            openHome(username);
            return true;
        } else return false;
    }
    //endregion

    //region ChatManager

    /**
     * Receives call from GUI to invite one of our friends
     * @param friendName the friend we want to invite
     * @return ChatParticipator if all is successful
     * @throws RemoteException
     * @throws ClientNotOnlineException
     */
    @Override
    public ChatParticipator sendInvite(String friendName) throws RemoteException, UnknownClientException {
        ChatParticipator cp = chatManager.sendInvite(friendName);
        if (cp != null)
            openChat(cp);
        else System.out.println("tis naar de kloten");
        return cp;
    }

    /**
     * invites another user to the already existing chatsession
     * @param cp the active ChatParticipator
     * @param friendName Friend's name
     * @return true if the invite was successful, otherwise false or error
     * @throws RemoteException
     * @throws ClientNotOnlineException
     */
    @Override
    public boolean inviteToSession(ChatParticipator cp, String friendName) throws RemoteException, UnknownClientException, ClientNotOnlineException {
        return chatManager.inviteToSession(cp, friendName);
    }

    @Override
    public boolean invite(IChatSession chatSession) throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public boolean leaveSession(ChatParticipator chatParticipator) throws RemoteException {
        boolean successfull = chatManager.leaveSession(chatParticipator);
        if (successfull) {
            ChatPage toBeClosed = chatPageHashMap.remove(chatParticipator);
            toBeClosed.setVisible(false);
            toBeClosed.dispose();
            return true;
        } else return false;
    }

    @Override
    public void pushMessage(ChatParticipator chatParticipator, String msg) throws Exception {
        chatManager.pushMessage(chatParticipator, msg);
    }

    @Override
    public void notifyView(ChatNotificationType cnt, IMessage msg, ChatParticipator participator) {
        if (cnt == ChatNotificationType.NEWMESSAGE) {
            chatPageHashMap.get(participator).receiveMessage(msg);
        }
    }

    /**
     * This gets invoked by ClientListener who received an update about the user's friends
     * @param cst The Client Status Type
     * @param friendName User friend's name
     */
    @Override
    public void notifyView(ClientStatusType cst, String friendName) {
        ClientStatusType clientStatusType = friends.get(friendName);
        if (clientStatusType == null) {
            System.out.println("We don't have this user as our friend...");
            return;
        }
        if (clientStatusType == cst) {
            System.out.println("We already have the latest status...");
            return;
        }

        if (cst == ClientStatusType.USERONLINE) {
            friends.put(friendName, cst);
        } else if (cst == ClientStatusType.USEROFFLINE) {
            friends.put(friendName, cst);
        } else if (cst == ClientStatusType.USERBUSY) {
            friends.put(friendName, cst);
        }
    }

    //endregion

    //region ClientManager
    @Override
    public void openPassive() throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public ArrayList<String> getFriends() throws RemoteException {
        return clientManager.getFriends();
    }

    @Override
    public boolean addFriend(String friendName) throws RemoteException, UnknownClientException {
        return clientManager.addFriend(friendName);
    }

    @Override
    public boolean deleteFriend(String friendName) throws RemoteException {
        return clientManager.deleteFriend(friendName);
    }

    @Override
    public void friendListUpdated() throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public ArrayList<IChatSession> getOfflineMessages() throws RemoteException {
        return clientManager.getOfflineMessages();
    }

    @Override
    public void offlineMessagesRead() throws RemoteException {
        clientManager.offlineMessagesRead();
    }

    //endregion
}
