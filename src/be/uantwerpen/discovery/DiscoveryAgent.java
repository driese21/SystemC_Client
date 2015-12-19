package be.uantwerpen.discovery;

import be.uantwerpen.interfaces.managers.IDiscoveryManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Dries on 19/12/2015.
 */
public class DiscoveryAgent {
    private MulticastSocket multicastSocket;
    private final String mCastAddress;
    private final int port;
    private final IDiscoveryManager discoveryManager;

    public DiscoveryAgent(IDiscoveryManager discoveryManager, String mCastAddress, int port) {
        this.discoveryManager = discoveryManager;
        this.mCastAddress = mCastAddress;
        this.port = port;
    }

    public void openSocket() throws IOException {
        this.multicastSocket = new MulticastSocket(port);
    }

    public DatagramPacket discover(String msg, int dstPort) throws IOException {
        byte[] bytesOut = msg.getBytes();
        DatagramPacket msgPacket = new DatagramPacket(bytesOut, bytesOut.length, InetAddress.getByName(mCastAddress), dstPort);
        this.multicastSocket.send(msgPacket);
        byte[] buf = new byte[8];
        DatagramPacket reply = new DatagramPacket(buf, buf.length);
        this.multicastSocket.receive(reply);
        return reply;
    }
}
