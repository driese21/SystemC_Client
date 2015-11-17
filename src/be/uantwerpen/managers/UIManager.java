package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.chat.Message;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.enums.ClientStatusType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.InvalidCredentialsException;
import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.guiChatC.ChatPage;
import be.uantwerpen.guiChatC.HomePage;
import be.uantwerpen.guiChatC.Login;
import be.uantwerpen.guiChatC.Register;
import be.uantwerpen.interfaces.IAuthenticationManager;
import be.uantwerpen.interfaces.IChatManager;
import be.uantwerpen.interfaces.IClientManager;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatParticipator;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creator: Seb
 * Date: 8/11/2015
 */
public class UIManager implements UIManagerInterface {
    private HashMap<ChatParticipator, ChatPage> chatPageHashMap;
    private HashMap<String, ClientStatusType> friends;
    private HomePage homePage;
    private IChatManager chatManager;
    private IClientManager clientManager;
    private IAuthenticationManager authenticationManager;

    /*public UIManager(){
        openLogin();
    }*/

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

    public void openHome(String user){
        this.homePage = new HomePage(this);
    }

    @Override
    public void openChat(String friendUserName) throws Exception {
        ChatParticipator chatParticipator = chatManager.sendInvite(friendUserName);
        if(chatParticipator!=null){
            ChatPage chat = new ChatPage(chatParticipator.getChatName(), this, chatParticipator);
            //chatMap.put(tempId, chat);
            chatPageHashMap.put(chatParticipator, chat);
        } else{
            throw new Exception("Invite is niet gelukt, dit is ongekend terrein!");
        }
    }

    /**
     * GUI opens a new ChatPage because we received an invitation
     * @param chatParticipator Reference to a ChatParticipator created by ChatManager
     * @throws RemoteException
     */
    @Override
    public void openChat(ChatParticipator chatParticipator) throws RemoteException {
        ChatPage chatPage = new ChatPage(chatParticipator.getChatName(), this, chatParticipator);
        chatPageHashMap.put(chatParticipator, chatPage);
        System.out.println("hallo ik doe een chat open");
        updateHomePage();
    }

    @Override
    public ArrayList<ChatParticipator> getActiveChatSessions() {
        return new ArrayList<>(chatPageHashMap.keySet());
    }

    /**
     * GUI opens existing ChatSession using the id of a ChatParticipator
     * @param id The ID of a ChatParticipator which is stored in the list
     */
    public void openChat(int id) {
        IChatParticipator chatParticipator = findParticipator(id);
        chatPageHashMap.get(chatParticipator).setVisible(true);
    }

    private ChatParticipator findParticipator(int id) {
        Iterator it = chatPageHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ChatParticipator cp = (ChatParticipator) pair.getKey();
            if (cp.getId() == id) return cp;
        }
        return null;
    }

    private void updateHomePage() {
        homePage.updateChats();
    }

    @Override
    public void updateFriendList(ArrayList<String> friends) {
        homePage.updateFriendList(friends);
    }

    //region AuthenticationManager
    @Override
    public boolean register(String username, String password, String fullName) {
        if (authenticationManager.register(username, password, fullName)) {
            openHome(username);
            return true;
        } return false;
    }

    @Override
    public boolean login(String username, String password) throws InvalidCredentialsException {
        if (authenticationManager.login(username,password)) {
            openHome(username);
            return true;
        } else return false;
    }
    //endregion

    //region ChatManager
    @Override
    public ChatParticipator sendInvite(String friendName) throws RemoteException, ClientNotOnlineException {
        //System.out.println("I want to invite " + friendName);
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
    public boolean sendInvite(ChatParticipator cp, String friendName) throws RemoteException, ClientNotOnlineException {
        return chatManager.sendInvite(cp, friendName);
    }

    @Override
    public boolean invite(IChatSession chatSession) throws RemoteException {
        throw new NotImplementedException();
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
    public void openPassive(IChatManager chatManager) throws RemoteException {
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

    //endregion
}
