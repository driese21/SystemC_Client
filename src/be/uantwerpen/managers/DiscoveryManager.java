package be.uantwerpen.managers;

import be.uantwerpen.discovery.DiscoveryAgent;
import be.uantwerpen.discovery.DiscoveryWatchdog;
import be.uantwerpen.interfaces.managers.IDiscoveryManager;
import be.uantwerpen.interfaces.managers.IMainManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Dries on 19/12/2015.
 */
public class DiscoveryManager extends Thread implements IDiscoveryManager {
    private final int MAXRETRIES = 5;
    private final IMainManager mainManager;
    private DiscoveryAgent discoveryAgent;
    private DiscoveryWatchdog discoveryWatchdog;
    private InetAddress serverIp;
    private int retries;
    private boolean waiting;

    public DiscoveryManager(IMainManager mainManager) {
        this.mainManager = mainManager;
        this.discoveryAgent = new DiscoveryAgent("224.0.0.230", 11335);
        this.discoveryWatchdog = new DiscoveryWatchdog(this, 5000);
        this.retries = 0;
    }

    private void startDiscovery() throws NotBoundException {
        try {
            this.discoveryAgent.openSocket();
            this.waiting = true;
            new Thread(discoveryWatchdog).start();
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
    public void discoveryReply(DatagramPacket reply) throws InterruptedException, RemoteException, NotBoundException, MalformedURLException {
        if (reply != null) {
            this.waiting = false;
            String rep = new String(reply.getData(),0,reply.getLength());
            if (rep.equalsIgnoreCase("SERVER")) {
                this.serverIp = reply.getAddress();
                this.discoveryAgent.closeSocket();
                this.discoveryWatchdog.interrupt();
                this.mainManager.chatServerFound(true);
            } else {
                retryDiscovery();
            }
        }
    }

    @Override
    public InetAddress getServerIp() {
        return serverIp;
    }

    @Override
    public void interruptManager() throws NotBoundException, MalformedURLException, RemoteException {
        if (waiting) {
            System.out.println("Watchdog interrupted...");
            this.discoveryAgent.setClosedByInterrupt(true);
            this.discoveryAgent.closeSocket();
            retryDiscovery();
        }
    }

    private void retryDiscovery() throws NotBoundException, MalformedURLException, RemoteException {
        if (retries < MAXRETRIES) {
            retries++;
            startDiscovery();
        } else mainManager.chatServerFound(false);
    }

    @Override
    public void run() {
        super.run();
        try {
            startDiscovery();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
