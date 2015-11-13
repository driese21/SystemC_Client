package be.uantwerpen.guiChatC;

import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.managers.*;
import be.uantwerpen.managers.UIManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private String chatName;
    private String[] gesprekken = {"Michiel","Dries","Sebastiaan","Djamo"};

    public HomePage(UIManagerInterface manager, String username) {
        super("HomePage");
        this.manager = manager;
        lstConversation.setListData(gesprekken);
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
                String friendName = JOptionPane.showInputDialog(HomePage.this,"Vul naam vriend in:");
              //  lstConversation.add(friendName)
            }
        });

        btnDeleteFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(HomePage.this, "Functie deleteFriend toevoegen");
            }
        });

        btnLogOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(HomePage.this, "Functie logOff toevoegen");
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

}
