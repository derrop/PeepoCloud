package net.nevercloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.nevercloud.node.NeverCloudNode;

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

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract void execute(CommandSender sender, String commandLine, String[] args);

    public String getUsage() {
        String usage = NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.usage." + this.name);
        if (usage != null)
            return usage;
        return NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.usage.notDefined");
    }

}
