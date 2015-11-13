package be.uantwerpen.guiChatC;

import be.uantwerpen.chat.Message;
import be.uantwerpen.client.Client;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

/**
 * Created by Djamo on 30/10/2015.
 */
public class ChatPage extends JFrame {
    private UIManagerInterface uiManagerInterface;
    private JPanel pnlRootPanel;
    private JButton btnInvite;
    private JButton btnLeaveConversation;
    private JTextField txtMessage;
    private JTextArea txtConversation;
    private JLabel lblInGesprekMet;

    public Client client;

    public ChatPage(String chatname, UIManagerInterface uiManagerInterface) {
        super(chatname);
        this.uiManagerInterface = uiManagerInterface;
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
                //uiManagerInterface.pushMessage();
                txtConversation.append(txtMessage.getText());
                txtConversation.append("\n");
                txtMessage.setText("");
            }
        });

        btnInvite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String friendName = JOptionPane.showInputDialog(ChatPage.this, "Vul naam vriend in:");
                    //client.invite(friendName);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ChatPage.this, ex.getMessage());
                }
            }
        });

        btnLeaveConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(ChatPage.this, "Functie leaveConversation toevoegen");
            }
        });
    }

    public void receiveMessage(IMessage message) throws RemoteException {
        txtConversation.append(message.getUsername() + ": " + message.getMessage());
        txtConversation.append("\n");
    }
}
