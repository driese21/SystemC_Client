package be.uantwerpen;

import be.uantwerpen.consoleManager.ConsoleManager;
import be.uantwerpen.exceptions.InvalidCredentialsException;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

/**
 * Created by Dries on 16/10/2015.
 */
public class ClientChat {
    public static void main(String[] args) {
        try {
            ConsoleManager cm = new ConsoleManager();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
        }
    }
}
