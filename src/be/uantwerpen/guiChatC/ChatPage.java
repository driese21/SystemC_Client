package be.uantwerpen.guiChatC;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.client.Client;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

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

    public Client client;

    public ChatPage(String chatname, UIManagerInterface uiManagerInterface, ChatParticipator chatParticipator) {
        super(chatname);
        this.uiManagerInterface = uiManagerInterface;
        this.chatParticipator = chatParticipator;
        lblInGesprekMet.setText(chatname);
        setContentPane(pnlRootPanel);
        pack();
        //  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addListeners();
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
                    //client.invite(friendName);
                    uiManagerInterface.sendInvite(friendName);

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
