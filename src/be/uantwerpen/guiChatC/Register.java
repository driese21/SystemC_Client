package be.uantwerpen.guiChatC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Michiel on 30/10/2015.
 */
public class Register extends JFrame {
    private JTextField txtFullName;
    private JTextField txtPw;
    private JTextField txtConfirmPw;
    private JButton btnRegister;
    private JTextField txtUserName;
    private JLabel lblFullName;
    private JLabel lblUserName;
    private JLabel lblPw;
    private JLabel lblConfirmPW;
    private JPanel pnlRegister;

    public Register() {
        super("Nieuwe gebruiker registreren");
        setContentPane(pnlRegister);
        pack();
        setVisible(true);

        addListeners();

    }

    private void addListeners() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                TO DO:  Gegevens lezen en vergelijken met de server
                        Als de gegevens al bestaan: gewoon de gebruiker inloggen
                        Als de gegevens niet bestaan: nieuwe gebruiker maken en inloggen
                 */
                dispose();
            }
        });
    }

}
