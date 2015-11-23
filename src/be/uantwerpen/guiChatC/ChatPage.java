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
    private String friendName;

    public ChatPage(String chatName, UIManagerInterface uiManagerInterface, ChatParticipator chatParticipator) {
        super(chatName);
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

    public ChatPage(String chatName, UIManagerInterface uiManagerInterface, IChatSession ics) {
        super(chatName);
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

        btnLeaveConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(ChatPage.this, "Functie leaveConversation toevoegen");
                setVisible(false);
            }
        });
    }

    public void receiveMessage(IMessage message) {
        try {
            txtConversation.append(message.getUsername() + ": " + message.getMessage());
            txtConversation.append("\n");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
