package net.peepocloud.plugin.bukkit.command.subcommand;


import org.bukkit.command.CommandExecutor;

public class SubCommandExecutor {
    private String name;
    private CommandExecutor executor;
    private String description;
    private String usage;

    public SubCommandExecutor(String name, CommandExecutor executor, String description, String usage) {
        this.name = name;
        this.executor = executor;
        this.description = description;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
