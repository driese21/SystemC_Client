package be.uantwerpen.interfaces.managers;

import be.uantwerpen.exceptions.InvalidCredentialsException;

/**
 * Created by Dries on 3/11/2015.
 */
public interface IAuthenticationManager {
    boolean register(String username, String password, String fullName);

    boolean login(String username, String password) throws InvalidCredentialsException;
}
