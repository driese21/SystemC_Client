package be.uantwerpen;

import be.uantwerpen.consoleManager.ConsoleManager;
import be.uantwerpen.exceptions.ClientNotOnlineException;
import be.uantwerpen.exceptions.InvalidCredentialsException;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

/**
 * Created by Dries on 16/10/2015.
 */
public class ClientChat {
    public static void main(String[] args) {
        try {
            try {
                ConsoleManager cm = new ConsoleManager();
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
        }
    }
}
