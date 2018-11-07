package net.nevercloud.node.commands;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

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
        return "no usage defined";
    }

}
