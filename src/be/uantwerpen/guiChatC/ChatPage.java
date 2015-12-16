package be.uantwerpen.guiChatC;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.interfaces.managers.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatSession;
import be.uantwerpen.rmiInterfaces.IMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Djamo on 30/10/2015.
 * Page you see when you are chatting with someone
 */
public class ChatPage extends JFrame {
    private UIManagerInterface uiManagerInterface;
    private ChatParticipator chatParticipator;

    private JPanel pnlRootPanel;
    private JButton btnInvite;
    private JButton btnLeaveConversation;
    private JTextField txtMessage;
    private JTextArea txtConversation;
    private JLabel lblInGesprekMet;
    private JLabel lblMe;
    private String friendName;

    public ChatPage(String chatName, UIManagerInterface uiManagerInterface, ChatParticipator chatParticipator) throws RemoteException {
        super(chatName);
        lblMe.setText(chatParticipator.getUserName());
        this.uiManagerInterface = uiManagerInterface;
        this.chatParticipator = chatParticipator;
        lblInGesprekMet.setText(chatName);
        setContentPane(pnlRootPanel);
        pack();
        //  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        addListeners();
    }

    //offline chatsessie
    public ChatPage(String chatName, UIManagerInterface uiManagerInterface, IChatSession ics) throws RemoteException {
        super(chatName);
        lblMe.setText(chatParticipator.getUserName());
        this.uiManagerInterface = uiManagerInterface;
        try {
            ArrayList<String> messages = uiManagerInterface.getMessages(ics);
            for (String msg : messages) {
                txtConversation.append(msg);
                txtConversation.append("\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        lblInGesprekMet.setText(chatName);
        setContentPane(pnlRootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void addListeners() {
        //Listener voor bericht te versturen
        /**
         * contains the message to send and clears the textbox after sending a message
         */
        txtMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    uiManagerInterface.pushMessage(chatParticipator, txtMessage.getText());
                    txtMessage.setText("");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        /**
         * Invite a friend into a conversation
         */
        btnInvite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    friendName = JOptionPane.showInputDialog(ChatPage.this, "Vul naam vriend in:");
                    uiManagerInterface.inviteToSession(chatParticipator, friendName);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ChatPage.this, ex.getMessage());
                }
            }
        });

        /**
         *  Leaves a conversation but remain online
         */
        btnLeaveConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    uiManagerInterface.leaveSession(chatParticipator);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                //setVisible(false);
            }
        });
    }

    /**
     * When a message is received it will put it into the txtconversation field
     * @param message the message it received
     */
    public void receiveMessage(IMessage message) {
        try {
            txtConversation.append(message.getUsername() + ": " + message.getMessage());
            txtConversation.append("\n");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
