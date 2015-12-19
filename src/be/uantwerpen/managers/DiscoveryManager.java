package be.uantwerpen.managers;

import be.uantwerpen.discovery.DiscoveryAgent;
import be.uantwerpen.interfaces.managers.IDiscoveryManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by Dries on 19/12/2015.
 */
public class DiscoveryManager implements IDiscoveryManager {
    private final int MAXRETRIES = 5;
    private DiscoveryAgent discoveryAgent;
    private InetAddress serverIp;
    private int retries;

    public DiscoveryManager() {
        this.discoveryAgent = new DiscoveryAgent(this, "224.0.0.230", 11335);
        this.retries = 0;
        startDiscovery();
    }

    private void startDiscovery() {
        try {
            this.discoveryAgent.openSocket();
            try {
                discoveryReply(this.discoveryAgent.discover("CLIENT", 11336));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void discoveryReply(DatagramPacket reply) throws InterruptedException {
        String rep = new String(reply.getData(),0,reply.getLength());
        if (rep.equalsIgnoreCase("SERVER")) {
            this.serverIp = reply.getAddress();
        } else {
            if (retries < MAXRETRIES) {
                Thread.sleep(1000);
                retries++;
                startDiscovery();
            } else return;
        }
    }

    @Override
    public InetAddress getServerIp() {
        return serverIp;
    }
}
