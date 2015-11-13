package be.uantwerpen.managers;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.chat.Message;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.guiChatC.ChatPage;
import be.uantwerpen.guiChatC.HomePage;
import be.uantwerpen.guiChatC.Login;
import be.uantwerpen.guiChatC.Register;
import be.uantwerpen.interfaces.IAuthenticationManager;
import be.uantwerpen.interfaces.IChatManager;
import be.uantwerpen.interfaces.IClientManager;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatParticipator;

import javax.swing.*;
import java.rmi.RemoteException;
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
}
