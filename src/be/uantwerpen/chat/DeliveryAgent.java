package be.uantwerpen.chat;

import be.uantwerpen.enums.ChatNotificationType;
import be.uantwerpen.rmiInterfaces.IChatParticipator;

import java.rmi.RemoteException;
import java.util.HashSet;

/**
 * Created by Dries on 19/11/2015.
 */
public class DeliveryAgent extends Thread {
    private HashSet<ChatParticipatorKey> participators;
    private Message message;
    private ChatParticipatorKey cpk;
    private ChatNotificationType chatNotificationType;

    public DeliveryAgent(HashSet<ChatParticipatorKey> participators, Message message, ChatNotificationType cnt) {
        this.participators = participators;
        this.message = message;
        this.chatNotificationType = cnt;
    }

    public DeliveryAgent(HashSet<ChatParticipatorKey> participators, ChatParticipatorKey cpk, ChatNotificationType chatNotificationType) {
        this.participators = participators;
        this.cpk = cpk;
        this.chatNotificationType = chatNotificationType;
    }

    private void deliver() {
        HashSet<ChatParticipatorKey> failedDelivery = new HashSet<>(participators.size());
        participators.forEach(chatParticipatorKey -> {
            if (!deliver(chatParticipatorKey.getParticipator())) failedDelivery.add(chatParticipatorKey);
        });
        if (failedDelivery.size()==0) return;
        failedDelivery.forEach(chatParticipatorKey -> {
            int retries = 0;
            while (retries < 5) {
                try {
                    if (!deliver(chatParticipatorKey.getParticipator())) System.out.println(chatParticipatorKey.getUserName() + " is unreachable...");
                    retries++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean deliver(IChatParticipator participator) {
        try {
            if (message != null) {
                participator.notifyListener(message);
            }
            else if (cpk != null) {
                if (chatNotificationType == ChatNotificationType.USERJOINED) participator.notifyListener(cpk.getParticipator(), cpk.isHost());
                else if (chatNotificationType == ChatNotificationType.USERLEFT) participator.notifyListener(cpk.getUserName());
            }
            else System.out.println("Both message and participator are null");
        } catch (RemoteException e) {
            e.printStackTrace();
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
