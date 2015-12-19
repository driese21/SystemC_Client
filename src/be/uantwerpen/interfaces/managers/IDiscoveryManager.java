package be.uantwerpen.interfaces.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 19/12/2015.
 */
public interface IDiscoveryManager {
    void discoveryReply(DatagramPacket reply) throws InterruptedException, RemoteException, NotBoundException, MalformedURLException;
    InetAddress getServerIp();
    void interrupt() throws NotBoundException, MalformedURLException, RemoteException;
}
