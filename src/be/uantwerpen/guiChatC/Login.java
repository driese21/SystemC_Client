package be.uantwerpen.guiChatC;

import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.exceptions.InvalidCredentialsException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Michiel on 30/10/2015.
 */
public class Login extends JFrame {
    private UIManagerInterface manager;
    private JPanel pnlLogin;
    private JLabel lblLogin;
    private JTextField txtUserName;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblUserName;
    private JLabel lblPw;
    private JPasswordField pwfPW;

    public Login(UIManagerInterface manager) {
        super("Aanmelden");
        this.manager = manager;
        setContentPane(pnlLogin);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addListeners();
    }

    private void addListeners() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //gegevens lezen
                String user = txtUserName.getText();
                char[] pwChar = pwfPW.getPassword();
                String pwInput = String.valueOf(pwChar);
                String pwOutput = pwInput.hashCode()+"";

                if (user.equals("") | pwInput.equals(""))  {
                    JOptionPane.showMessageDialog(null,"Voer een geldige gebruikersnaam en wachtwoord in!");
                }
                else{
                    //inloggen
                    try{
                        manager.login(user, pwOutput);
                        dispose();
                    } catch (InvalidCredentialsException ex) {
                        JOptionPane.showMessageDialog(null,"De gebruiker bestaat nog niet, probeer te registreren!");
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Register registerForm = new Register();
                manager.openRegister();
                dispose();
            }
        });
    }
}
