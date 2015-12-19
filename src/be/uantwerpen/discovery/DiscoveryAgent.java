package be.uantwerpen.discovery;

import be.uantwerpen.interfaces.managers.IDiscoveryManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 * Created by Dries on 19/12/2015.
 */
public class DiscoveryAgent {
    private MulticastSocket multicastSocket;
    private final String mCastAddress;
    private final int port;
    private boolean closedByInterrupt;

    public DiscoveryAgent(String mCastAddress, int port) {
        this.mCastAddress = mCastAddress;
        this.port = port;
    }

    public void openSocket() throws IOException {
        this.multicastSocket = new MulticastSocket(port);
        this.setClosedByInterrupt(false);
    }

    public DatagramPacket discover(String msg, int dstPort) throws IOException {
        try {
            byte[] bytesOut = msg.getBytes();
            DatagramPacket msgPacket = new DatagramPacket(bytesOut, bytesOut.length, InetAddress.getByName(mCastAddress), dstPort);
            this.multicastSocket.send(msgPacket);
            byte[] buf = new byte[8];
            DatagramPacket reply = new DatagramPacket(buf, buf.length);
            this.multicastSocket.receive(reply);
            return reply;
        } catch (SocketException se) {
            if (!closedByInterrupt) throw se;
        }
        return null;
    }

    public void closeSocket() {
        this.multicastSocket.close();
    }

    public void setClosedByInterrupt(boolean closedByInterrupt) {
        this.closedByInterrupt = closedByInterrupt;
    }
}
