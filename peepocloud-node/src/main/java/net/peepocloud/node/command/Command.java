package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;

public abstract class Command {

    public Command(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public Command(String name, String permission) {
        this(name, permission, (String[]) null);
    }

    public Command(String name) {
        this(name, null);
    }

    private String name;
    private String permission;
    private String[] aliases;

    /**
     * Gets the name defined for this Command
     * @return the name of this Command
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the permission defined in this Command
     * @return the permission of this Command
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gets the aliases defined in this Command
     * @return the aliases of this Command
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Called when the Command was executed
     * @param sender the sender which has executed this Command
     * @param commandLine the command line sent by the sender
     * @param args the arguments given by the sender (always commandLine splitted by " " and removed the first argument)
     */
    public abstract void execute(CommandSender sender, String commandLine, String[] args);

    /**
     * Gets the usage of this Command
     * @return the usage of this Command
     */
    public String getUsage() {
        String usage = PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.usage." + this.name);
        if (usage != null)
            return usage;
        return PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.usage.notDefined");
    }

}
