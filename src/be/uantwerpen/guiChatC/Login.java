package be.uantwerpen.guiChatC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Michiel on 30/10/2015.
 */
public class Login extends JFrame {
    private JPanel pnlLogin;
    private JLabel lblLogin;
    private JTextField txtUserName;
    private JTextField txtPw;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblUserName;
    private JLabel lblPw;

    public Login() {
        super("Aanmelden");
        setContentPane(pnlLogin);
        pack();
        setVisible(true);

        addListeners();

    }

    private void addListeners() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                TO DO:  Gegevens lezen en de gebruiker aanmelden

                        nieuwe HomePage openen
                        //HomePage homepageForm = new HomePage();

                        Als de gegevens niet kloppen: error
                */
                dispose();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register registerForm = new Register();
                dispose();
            }
        });
    }
}
