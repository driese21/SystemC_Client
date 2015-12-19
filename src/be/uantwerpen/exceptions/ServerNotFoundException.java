package be.uantwerpen.exceptions;

/**
 * Created by Dries on 19/12/2015.
 */
public class ServerNotFoundException extends Exception {
    public ServerNotFoundException() {
    }

    public ServerNotFoundException(String message) {
        super(message);
    }
}
