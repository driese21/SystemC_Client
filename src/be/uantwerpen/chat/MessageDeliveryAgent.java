package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.rmiInterfaces.IChatParticipator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Dries on 19/11/2015.
 */
public class MessageDeliveryAgent extends Thread {
    private ArrayList<IChatParticipator> participators;
    private Message message;
    private ChatNotificationType chatNotificationType;

    public MessageDeliveryAgent(ArrayList<IChatParticipator> participators, Message message, ChatNotificationType cnt) {
        this.participators = participators;
        this.message = message;
        this.chatNotificationType = cnt;
    }

    private void deliver() {
        ArrayList<IChatParticipator> failedDelivery = new ArrayList<>(participators.size());
        failedDelivery.addAll(participators.stream().filter(chatParticipator -> !deliver(chatParticipator)).collect(Collectors.toList()));
        if (failedDelivery.size()==0) return;
        int retries=0;
        for (IChatParticipator participator : participators) {
            while (retries<5) {
                try {
                    if (!deliver(participator)) System.out.println(participator + " is unreachabl...");
                    retries++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            retries=0;
        }
    }

    private boolean deliver(IChatParticipator participator) {
        try {
            participator.notifyListener(chatNotificationType, message);
        } catch (RemoteException e) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        super.run();
        deliver();
    }
}
