package be.uantwerpen.interfaces.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by Dries on 19/12/2015.
 */
public interface IDiscoveryManager {
    void discoveryReply(DatagramPacket reply) throws InterruptedException;
    InetAddress getServerIp();
}
