package be.uantwerpen.guiChatC;

import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.managers.AuthenticationManager;
import be.uantwerpen.rmiInterfaces.IClientAcceptor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.Arrays;

/**
 * Created by Michiel on 30/10/2015.
 */
public class Register extends JFrame {
    private UIManagerInterface manager;
    private JTextField txtFullName;
    private JButton btnRegister;
    private JTextField txtUserName;
    private JLabel lblFullName;
    private JLabel lblUserName;
    private JLabel lblPw;
    private JLabel lblConfirmPW;
    private JPanel pnlRegister;
    private JPasswordField pwfPw;
    private JPasswordField pwfConfirmPw;

    public Register(UIManagerInterface manager) {
        super("Nieuwe gebruiker registreren");
        this.manager = manager;
        setContentPane(pnlRegister);
        pack();
        setVisible(true);

        addListeners();

        txtFullName.setText("Test User");
        txtUserName.setText("test@1337.com");
    }

    private void addListeners() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //gegevens lezen
                String fullName = txtFullName.getText();
                String user = txtUserName.getText();

                //password lezen en hashen
                String pwInput = (String.valueOf(pwfPw.getPassword()));
                String confirmInput = (String.valueOf(pwfConfirmPw.getPassword()));
                String pwOutput = pwInput.hashCode()+"";

                //Kijken of de velden juist ingevuld zijn
                if (user.equals("") | pwInput.equals("")| fullName.equals("")| confirmInput.equals(""))  {
                    JOptionPane.showMessageDialog(null,"Voer alle gegevens juist in!");
                }
                else if(Arrays.equals(pwfPw.getPassword(), pwfConfirmPw.getPassword() )){
                    //registreren
                    try{
                        //temporary code!!
                        IClientAcceptor ca = (IClientAcceptor) Naming.lookup("//" + "127.0.0.1:11337" + "/ChatServer");
                        //IChatServer cs = (IChatServer) Naming.lookup("//" + "localhost" + "/Chatserver");
                        //ca.register(user, pwOutput, fullName);
                        AuthenticationManager authenticationManager = new AuthenticationManager(ca);
                        authenticationManager.register(user, pwOutput, fullName);

                        //HomePage homepageForm = new HomePage(user);
                        manager.openHome(user);
                        dispose();
                    }
                    catch(Exception x){
                        System.out.println("Client exception" + x.getMessage());
                        x.printStackTrace();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null,"De wachtwoorden komen niet overeen!");
                    System.out.println(pwInput + " " + pwOutput);
            }
        }
    });
    }
}

