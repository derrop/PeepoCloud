package net.peepocloud.plugin.bukkit.command.subcommand;


import org.bukkit.command.CommandExecutor;

public abstract class SubCommandExecutor {
    private String name;
    private String description;
    private String usage;

    public SubCommandExecutor(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    public abstract CommandExecutor subExecutor();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
