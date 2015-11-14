package be.uantwerpen.client;

import be.uantwerpen.rmiInterfaces.IClientSession;

import java.rmi.RemoteException;

/**
 * Created by Dries on 14/11/2015.
 */
public class StatusUpdater implements Runnable {
    private IClientSession clientSession;

    public StatusUpdater() {
    }

    public StatusUpdater(IClientSession clientSession) {
        this.clientSession = clientSession;
    }

    private void updateStatus() {
        try {
            clientSession.updateStatus();
            try {
                Thread.sleep(10000);
                updateStatus();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Something went wrong while updating!");
            }
        } catch (RemoteException e) {
            System.out.println("Is the server offline maybe?");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        updateStatus();
    }
}
