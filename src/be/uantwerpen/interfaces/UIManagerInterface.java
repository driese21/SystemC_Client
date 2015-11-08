package be.uantwerpen.interfaces;

/**
 * Creator: Seb
 * Date: 8/11/2015
 */
public interface UIManagerInterface {
    void openLogin();

    void openRegister();

    void openHome(String user);

    void openChat(String chatName);
}
