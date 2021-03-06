package be.uantwerpen.guiChatC;

import be.uantwerpen.interfaces.managers.UIManagerInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Michiel on 30/10/2015.
 * Page you use to register a new user
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
    private JButton btnRandomUser;

    public Register(UIManagerInterface manager) {
        super("Nieuwe gebruiker registreren");
        this.manager = manager;
        setContentPane(pnlRegister);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        addListeners();

        txtFullName.setText("Test User");
        txtUserName.setText("test@1337.com");
    }

    private void addListeners() {

        /**
         * For testing purposes to create new user fast
         */
        btnRandomUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] peoples = {"Bob", "Jill", "Tom", "Brandon","Jef","Jos","Jan","Danny","Sebastiaan","Michiel","Dries","Djamo","Willem","Frederik","Anthony","Stijn","Jonas","Shaniqua","Tyrone","Tyrell","Micheal","Philip","Tywinn","Charrls","MVP","Gwuido","Luc","SnelleRuddi","Ron","Harry","Hermelien","Willy"};

                List<String> names = Arrays.asList(peoples);
                int index = new Random().nextInt(names.size());
                String name = names.get(index);
                txtFullName.setText(name);
                txtUserName.setText(name+"@chatC.be");
                pwfConfirmPw.setText("test");
                pwfPw.setText("test");

              //  JOptionPane.showMessageDialog(Register.this, "Uw naam is:" + name);


            }
        });
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
                        manager.register(user, pwOutput, fullName);
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

