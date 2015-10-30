package be.uantwerpen.guiChatC;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Djamo on 30/10/2015.
 */
public class HomePage extends JFrame {
    private JButton btnAddFriend;
    private JButton btnDeleteFriend;
    private JButton btnLogOff;
    private JTextField txtSearchConversation;
    private JList lstConversation;
    private JPanel pnlRootPanel;

    public HomePage() {

        super("HomePage");
        String gesprekken[] = {"Michiel","Dries","Sebastiaan","Djamo"};
        lstConversation =  new JList(gesprekken);

        setContentPane(pnlRootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        addListeners();
        setVisible(true);


    }

    //Actionlisteners voor buttons
    public void addListeners(){
        btnAddFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(HomePage.this, "Functie addFriend toevoegen");
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

        lstConversation.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                JOptionPane.showConfirmDialog(HomePage.this, "Functie chatten toevoegen");
            }
        });

    }

    public void fillList(){

    }
}
