package be.uantwerpen.guiChatC;

import be.uantwerpen.chat.ChatSession;
import be.uantwerpen.rmiInterfaces.IChatServer;
import be.uantwerpen.rmiInterfaces.IClientSession;

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
                //gegevens lezen
                String user = txtUserName.getText();
                String pw = txtPw.getText();

                if (user.equals("") | pw.equals(""))  {
                    JOptionPane.showMessageDialog(null,"Voer een geldige gebruikersnaam en wachtwoord in!");
                }
                else{
                    //inloggen
                    try{
                        IChatServer cs = (IChatServer) Naming.lookup("//" + "127.0.0.1:11337" + "/ChatServer");
                        //IChatServer cs = (IChatServer) Naming.lookup("//"+"localhost"+"/Chatserver");
                        cs.login(user,pw);
                    }
                    catch(Exception x){
                        System.out.println("Client exception" + x.getMessage());
                        x.printStackTrace();
                    }

                    HomePage homepageForm = new HomePage();
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
