package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.chat.Message;
import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.InvalidCredentialsException;
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

import javax.swing.*;
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
    //private HashMap<Integer, ChatPage> chatMap = new HashMap<>();
    private HashMap<IChatParticipator, ChatPage> chatPageHashMap;
    //private ChatManager cm = new ChatManager();
    private IChatManager chatManager;
    private IClientManager clientManager;
    private IAuthenticationManager authenticationManager;

    /*public UIManager(){
        openLogin();
    }*/

    public UIManager(IChatManager chatManager, IClientManager clientManager, IAuthenticationManager authenticationManager) {
        this.chatPageHashMap = new HashMap<>();
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
        HomePage homepageForm = new HomePage(this, user);
    }

    @Override
    public void openChat(String friendUserName) throws Exception {
        IChatParticipator chatParticipator = chatManager.sendInvite(friendUserName);
        if(chatParticipator!=null){
            ChatPage chat = new ChatPage(chatParticipator.getChatName());
            //chatMap.put(tempId, chat);
            chatPageHashMap.put(chatParticipator, chat);
        } else{
            throw new Exception("Invite is niet gelukt, dit is ongekend terrein!");
        }
        /*//todo Check if this shit is legit or not, replace invite value with proper username
        try {
            IChatParticipator chatParticipator = chatManager.sendInvite(friendUserName);
            if(chatParticipator!=null){
                ChatPage chat = new ChatPage(chatParticipator.getChatName());
                //chatMap.put(tempId, chat);
                chatPageHashMap.put(chatParticipator, chat);
            } else{
                throw new Exception("Invite is niet gelukt, dit is ongekend terrein!");
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Remote exception!\n" + e.getMessage());
        } catch (ClientNotOnlineException e) {
            JOptionPane.showMessageDialog(null, "Deze user is niet online!");
        }*/
    }

    /**
     * GUI opens a new ChatPage because we received an invitation
     * @param chatParticipator Reference to a ChatParticipator created by ChatManager
     * @throws RemoteException
     */
    @Override
    public void openChat(IChatParticipator chatParticipator) throws RemoteException {
        ChatPage chatPage = new ChatPage(chatParticipator.getChatName());
        chatPageHashMap.put(chatParticipator, chatPage);
    }

    /**
     * GUI opens existing ChatSession using the id of a ChatParticipator
     * @param id The ID of a ChatParticipator which is stored in the list
     */
    public void openChat(int id) {
        IChatParticipator chatParticipator = findParticipator(id);
        chatPageHashMap.get(chatParticipator).setVisible(true);
    }

    /*public void openChat(int id, String chatName){
        ChatPage chat = new ChatPage(chatName);
        chatMap.put(id, chat);
    }*/

    public void receiveMessage(int id, Message message){
        //chatMap.get(id).receiveMessage(message);
        chatPageHashMap.get(findParticipator(id)).receiveMessage(message);
        /*Iterator it = chatPageHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ChatParticipator cp = (ChatParticipator) pair.getKey();
            if (cp.getId() == id) ((ChatPage)pair.getValue()).receiveMessage(message);
        }*/
    }

    private IChatParticipator findParticipator(int id) {
        Iterator it = chatPageHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ChatParticipator cp = (ChatParticipator) pair.getKey();
            if (cp.getId() == id) return cp;
        }
        return null;
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
    public IChatParticipator sendInvite(String friendName) throws RemoteException, ClientNotOnlineException {
        return chatManager.sendInvite(friendName);
    }

    @Override
    public boolean invite(IChatSession chatSession) throws RemoteException {
        return chatManager.invite(chatSession);
    }

    @Override
    public void pushMessage(ChatParticipator chatParticipator, String msg) throws Exception {
        chatManager.pushMessage(chatParticipator, msg);
    }

    @Override
    public void notifyView(ChatNotificationType cnt, IMessage msg, IChatParticipator participator) throws Exception {
        throw new Exception("Needs implementation");
    }
    //endregion

    //region ClientManager
    @Override
    public void openPassive(IChatManager chatManager) throws RemoteException {
        clientManager.openPassive(chatManager);
    }

    @Override
    public ArrayList<String> getFriends() throws RemoteException {
        return clientManager.getFriends();
    }

    @Override
    public boolean addFriend(String friendName) throws RemoteException {
        return clientManager.addFriend(friendName);
    }

    @Override
    public boolean deleteFriend(String friendName) throws RemoteException {
        return clientManager.deleteFriend(friendName);
    }
    //endregion
}
