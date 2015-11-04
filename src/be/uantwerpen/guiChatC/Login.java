package be.uantwerpen.guiChatC;

import be.uantwerpen.rmiInterfaces.IClientAcceptor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

/**
 * Created by Michiel on 30/10/2015.
 */
public class Login extends JFrame {
    private JPanel pnlLogin;
    private JLabel lblLogin;
    private JTextField txtUserName;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblUserName;
    private JLabel lblPw;
    private JPasswordField pwfPW;

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
                        IClientAcceptor ca = (IClientAcceptor) Naming.lookup("//" + "127.0.0.1:11337" + "/ChatServer");
                        //IChatServer cs = (IChatServer) Naming.lookup("//"+"localhost"+"/Chatserver");
                        ca.login(user,pwOutput);

                        HomePage homepageForm = new HomePage(user);
                    }
                    catch(Exception x){
                        System.out.println("Client exception" + x.getMessage());
                        x.printStackTrace();
                    }

                }
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
