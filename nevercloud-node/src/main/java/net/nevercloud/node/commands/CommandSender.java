package net.nevercloud.node.commands;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

public interface CommandSender {

    default void sendMessage(String... messsages) {
        for (String message : messsages) {
            this.sendMessage(message);
        }
    }

    String getName();

    void sendMessage(String message);

    boolean hasPermission(String permission);

}
