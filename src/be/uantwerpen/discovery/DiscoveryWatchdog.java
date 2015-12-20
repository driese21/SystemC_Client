package be.uantwerpen.discovery;

import be.uantwerpen.interfaces.managers.IDiscoveryManager;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Creates interrupts every 10 seconds
 * Created by Dries on 19/12/2015.
 */
public class DiscoveryWatchdog extends Thread {
    private final int sleepTime;
    private final IDiscoveryManager discoveryManager;

    public DiscoveryWatchdog(IDiscoveryManager discoveryManager,int sleepTime) {
        this.discoveryManager = discoveryManager;
        this.sleepTime = sleepTime;
    }


    private void waitAndInterruptManager() throws InterruptedException, RemoteException, NotBoundException, MalformedURLException {
        Thread.sleep(sleepTime);
        discoveryManager.interruptManager();
    }

    @Override
    public void run() {
        super.run();
        try {
            try {
                waitAndInterruptManager();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            if (e.getMessage().equalsIgnoreCase("sleep interrupted")) {
            }
            else e.printStackTrace();
        }
    }
}
