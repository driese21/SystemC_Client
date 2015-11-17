package be.uantwerpen;

import be.uantwerpen.guiChatC.HomePage;
import be.uantwerpen.interfaces.UIManagerInterface;
import be.uantwerpen.managers.MainManager;
import be.uantwerpen.managers.UIManager;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 16/10/2015.
 */
public class ClientChat {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

        MainManager mainManager = new MainManager();

        //UIManagerInterface manager = new UIManager();1
        //Login login = new Login();
        /*try {
            try {
                System.out.println(args[0] + "  " + args[1]);
                ConsoleManager cm = new ConsoleManager(args[0],args[1]);
            } catch (ClientNotOnlineException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
        }*/
    }
}
