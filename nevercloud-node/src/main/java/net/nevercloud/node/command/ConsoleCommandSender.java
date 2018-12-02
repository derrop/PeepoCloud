package net.nevercloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

/**
 * Represents the console as a {@link CommandSender}
 */
public class ConsoleCommandSender implements CommandSender {
    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
