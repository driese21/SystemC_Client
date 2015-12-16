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
 * Default page when you log in
 */
public class HomePage extends JFrame {
    private UIManagerInterface manager;
    private JButton btnAddFriend;
    private JButton btnDeleteFriend;
    private JButton btnLogOff;

    private DefaultListModel conversationListModel;
    private JList lstConversation;

    private JPanel pnlRootPanel;
    private DefaultListModel friendsListModel;
    private JLabel lblFriends;
    private JList lstFriends;

    private String friendName;


    private ArrayList<ChatParticipator> gesprekken;

    public HomePage(UIManagerInterface manager, String name) {
        super("HomePage of " + name);
        this.manager = manager;
        this.conversationListModel = new DefaultListModel();
        this.friendsListModel = new DefaultListModel();
        this.lstConversation.setModel(conversationListModel);
        this.lstFriends.setModel(friendsListModel);
        setContentPane(pnlRootPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addListeners();
        setVisible(true);
    }

    public void addListeners(){
        /**
         *  Adds a friend to friendlist
         */
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

        /**
         * Deletes a friend from friendlist
         */
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


        /**
         * Log off the system
         */
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
                if (!e.getValueIsAdjusting()) {
                    try {
                        System.out.println("clicked it!");
                        ChatParticipator chatParticipator = (ChatParticipator) lstConversation.getSelectedValue();
                        manager.openChat(chatParticipator);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        addFriendsActionListener();
    }

    private void addFriendsActionListener() {
        lstFriends.addListSelectionListener(getFriendsActionListener());
    }

    private ListSelectionListener getFriendsActionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    try {
                        manager.sendInvite(lstFriends.getSelectedValue().toString());
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    } catch (UnknownClientException uce) {
                        uce.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * Updates the list with friends
     * @param friends the user's friends
     */
    public void updateFriendList(ArrayList<String> friends) {
        friendsListModel.clear();
        friends.forEach(friendsListModel::addElement);
    }

    /**
     * Updates the chats of the participators
     */
    public void updateChats() {
        conversationListModel.clear();
        ArrayList<ChatParticipator> participators = manager.getActiveChatSessions();
        participators.forEach(conversationListModel::addElement);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
