package be.uantwerpen.guiChatC;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.managers.*;
import be.uantwerpen.managers.UIManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Djamo on 30/10/2015.
 */
public class HomePage extends JFrame {
    private UIManagerInterface manager;
    private JButton btnAddFriend;
    private JButton btnDeleteFriend;
    private JButton btnLogOff;
    private JTextField txtSearchConversation;
    private JList lstConversation;
    private JPanel pnlRootPanel;
    private JComboBox cmbFriends;

    private String friendName;

    private String chatName;
    //private String[] gesprekken = {"Michiel","Dries","Sebastiaan","Djamo"};
    private ArrayList<String> gesprekken;

    public HomePage(UIManagerInterface manager) {
        super("HomePage");
        this.manager = manager;
        try {
            updateFriendList(manager.getFriends());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        setContentPane(pnlRootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addListeners();
        setVisible(true);
    }

    public void addListeners(){
        txtSearchConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnAddFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendName = JOptionPane.showInputDialog(HomePage.this,"Vul naam vriend in:");
                try {
                    manager.addFriend(friendName);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HomePage.this,ex.getMessage());
                }
              // lstConversation.add(friendName)
            }
        });

        btnDeleteFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendName = JOptionPane.showInputDialog(HomePage.this,"Vul naam (vroegere) vriend in:");
                try {
                    manager.deleteFriend(friendName);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HomePage.this,ex.getMessage());
                }
            }
        });

        btnLogOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        //Verander gesprek
        lstConversation.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                JOptionPane.showMessageDialog(HomePage.this,"Je will chatten met: " + lstConversation.getSelectedValue());
               chatName = lstConversation.getSelectedValue().toString();
                //ChatPage chat = new ChatPage(chatName);
                try {
                    manager.openChat(chatName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void addFriendsActionListener() {
        cmbFriends.addActionListener(getFriendsActionListener());
    }

    private void removeFriendsActionListener() {
        cmbFriends.removeActionListener(getFriendsActionListener());
    }

    private ActionListener getFriendsActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox jComboBox = (JComboBox) e.getSource();
                try {
                    manager.sendInvite((String)jComboBox.getSelectedItem());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (ClientNotOnlineException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    /**
     * Updates the ComboBox with friends
     * @param friends the user's friends
     */
    public void updateFriendList(ArrayList<String> friends) {
        removeFriendsActionListener();
        friends.forEach(fr -> cmbFriends.addItem(fr));
        addFriendsActionListener();
    }

    public void updateChatSession(ArrayList<String> chats) {
        this.gesprekken = chats;
        lstConversation.setListData(gesprekken.toArray());
    }

}
