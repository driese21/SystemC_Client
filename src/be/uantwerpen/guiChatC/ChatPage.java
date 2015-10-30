package be.uantwerpen.guiChatC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Djamo on 30/10/2015.
 */
public class ChatPage extends JFrame {
    private JPanel pnlRootPanel;
    private JButton btnAddPerson;
    private JButton btnLeaveConversation;
    private JTextField textField1;
    private JTextArea txtConversation;

    public ChatPage(){
        super("Chatgesprek");
        setContentPane(pnlRootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addListeners();

    }

    public void addListeners(){
        btnAddPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(ChatPage.this, "Functie addPerson toevoegen");
            }
        });

        btnLeaveConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(ChatPage.this, "Functie leaveConversation toevoegen");
            }
        });


    }
}
