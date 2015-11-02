package be.uantwerpen.guiChatC;

import be.uantwerpen.rmiInterfaces.IChatServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

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
                //gegevens lezen
                String fullName = txtFullName.getText();
                String user = txtUserName.getText();
                String pw = txtPw.getText();
                String confirm = txtConfirmPw.getText();

                if (user.equals("") | pw.equals("")| fullName.equals("")| confirm.equals(""))  {
                    JOptionPane.showMessageDialog(null,"Voer alle gegevens juist in!");
                }
                else if(pw.equals(confirm)){
                    //registreren
                    try{
                        IChatServer cs = (IChatServer) Naming.lookup("//" + "127.0.0.1:11337" + "/ChatServer");
                        //IChatServer cs = (IChatServer) Naming.lookup("//" + "localhost" + "/Chatserver");
                        cs.register(user, pw, fullName);
                    }
                    catch(Exception x){
                        System.out.println("Client exception" + x.getMessage());
                        x.printStackTrace();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null,"De wachtwoorden komen niet overeen!");
            }
        }
    });
    }
}

