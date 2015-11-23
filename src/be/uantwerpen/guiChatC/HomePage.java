package be.uantwerpen.guiChatC;

import be.uantwerpen.chat.ChatParticipator;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.UnknownClientException;
import be.uantwerpen.interfaces.managers.UIManagerInterface;
import be.uantwerpen.rmiInterfaces.IChatSession;

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

    private DefaultListModel conversationListModel;
    private JList lstConversation;

    private JPanel pnlRootPanel;
    private JComboBox cmbFriends;

    private String friendName;

    private ArrayList<ChatParticipator> gesprekken;

    public HomePage(UIManagerInterface manager) {
        super("HomePage");
        this.manager = manager;
        try {
            updateFriendList(manager.getFriends());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.conversationListModel = new DefaultListModel();
        lstConversation.setModel(conversationListModel);
        setContentPane(pnlRootPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addListeners();
        setVisible(true);
    }

    public void addListeners(){
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
                //ChatPage chat = new ChatPage(chatName);
                try {
                    manager.openChat((ChatParticipator) lstConversation.getSelectedValue());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        addFriendsActionListener();
    }

    private void addFriendsActionListener() {
        cmbFriends.addActionListener(getFriendsActionListener());
    }

    private ActionListener getFriendsActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox jComboBox = (JComboBox) e.getSource();
                try {
                    manager.sendInvite(jComboBox.getSelectedItem().toString());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (UnknownClientException uce) {
                    uce.printStackTrace();
                }
            }
        };
    }

    /**
     * Updates the ComboBox with friends
     * @param friends the user's friends
     */
    public void updateFriendList(ArrayList<String> friends) {
        for (int i=0;i<friends.size();i++) {
            //use insert item at to not trigger the action listener
            cmbFriends.insertItemAt(friends.get(i),i);
        }
        //addFriendsActionListener();
    }

    public void updateChats() {
        System.out.println("Updating conversation list");
        conversationListModel.clear();
        ArrayList<ChatParticipator> participators = manager.getActiveChatSessions();
        System.out.println("Amount of sessions I joined: " + participators.size());
        participators.forEach(conversationListModel::addElement);
    }

}
