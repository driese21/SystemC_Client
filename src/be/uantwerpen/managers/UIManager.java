package be.uantwerpen.managers;

import be.uantwerpen.chat.Message;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.guiChatC.ChatPage;
import be.uantwerpen.guiChatC.HomePage;
import be.uantwerpen.guiChatC.Login;
import be.uantwerpen.guiChatC.Register;
import be.uantwerpen.interfaces.UIManagerInterface;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Creator: Seb
 * Date: 8/11/2015
 */
public class UIManager implements UIManagerInterface {
    private HashMap<Integer, ChatPage> chatMap = new HashMap<>();
    private ChatManager cm = new ChatManager();

    public UIManager(){
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

    public void openChat(String chatName){
        //todo Check if this shit is legit or not, replace invite value with proper username
        try {
            int tempId = cm.sendInvite(chatName);
            if(tempId < 0){
                JOptionPane.showMessageDialog(null, "Invite niet gelukt!");
            } else{
                ChatPage chat = new ChatPage(chatName);
                chatMap.put(tempId, chat);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Remote exception!\n" + e.getMessage());
        } catch (ClientNotOnlineException e) {
            JOptionPane.showMessageDialog(null, "Deze user is niet online!");
        }
    }

    public void openChat(int id, String chatName){
        ChatPage chat = new ChatPage(chatName);
        chatMap.put(id, chat);
    }

    public void receiveMessage(int id, Message message){
        chatMap.get(id).receiveMessage(message);
    }
}
