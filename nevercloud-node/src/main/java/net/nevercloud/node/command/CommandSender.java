package net.nevercloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

public interface CommandSender {

    default void sendMessage(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    String getName();

    void sendMessage(String message);

    boolean hasPermission(String permission);

}
