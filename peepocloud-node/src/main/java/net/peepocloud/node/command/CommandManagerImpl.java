package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.addon.Addon;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandManager;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.logging.ColoredLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManagerImpl implements CommandManager {

    private final CommandSender console = new ConsoleCommandSender();

    private Map<String, CommandInfo> commands = new HashMap<>();
    private Thread commandReaderThread;

    public CommandManagerImpl(ColoredLogger logger) {
        logger.getConsoleReader().addCompleter(new CommandCompleter(this, logger));
        this.commandReaderThread = new Thread("ConsoleCommand Reader") {
            @Override
            public void run() {
                try {
                    String line;
                    while (!isInterrupted() && PeepoCloudNode.getInstance().isRunning() && (line = logger.readLine1()) != null) {
                        line = line.trim();
                        if (!"".equals(line) && !dispatchCommand(console, line)) {
                            System.out.println("Command not found, type &ehelp &rfor a list of all commands");
                        }
                    }
                } catch (UnsupportedOperationException e) {
                    if (e.getMessage().equalsIgnoreCase("read() with timeout cannot be called as non-blocking operation is disabled")) {
                        System.exit(0);
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.commandReaderThread.setDaemon(true);
        this.commandReaderThread.start();
    }

    public void shutdown() {
        this.commandReaderThread.interrupt();
    }

    /**
     * Registers commands in this CommandManagerImpl by their name and aliases
     * @param commands the commands to register
     * @return this
     */
    public CommandManagerImpl registerCommands(Command... commands) {
        for (Command command : commands) {
            CommandInfo info = new CommandInfo(command, null);
            this.commands.put(command.getName().toLowerCase(), info);
            if (command.getAliases() != null && command.getAliases().length != 0) {
                for (String alias : command.getAliases()) {
                    this.commands.put(alias.toLowerCase(), info);
                }
            }
        }
        return this;
    }

    @Override
    public CommandManager registerCommands(Addon addon, Command... commands) {
        Preconditions.checkNotNull(addon, "addon");
        for (Command command : commands) {
            CommandInfo info = new CommandInfo(command, addon);
            this.commands.put(command.getName().toLowerCase(), info);
            if (command.getAliases() != null && command.getAliases().length != 0) {
                for (String alias : command.getAliases()) {
                    this.commands.put(alias.toLowerCase(), info);
                }
            }
        }
        return this;
    }

    @Override
    public CommandManager registerCommand(Addon addon, Command command) {
        Preconditions.checkNotNull(addon, "addon");
        CommandInfo info = new CommandInfo(command, addon);
        this.commands.put(command.getName().toLowerCase(), info);
        if (command.getAliases() != null && command.getAliases().length != 0) {
            for (String alias : command.getAliases()) {
                this.commands.put(alias.toLowerCase(), info);
            }
        }
        return this;
    }

    /**
     * Unregisters commands in this CommandManagerImpl
     * @param commands the commands to unregister
     * @return this
     */
    public CommandManagerImpl unregisterCommands(Command... commands) {
        for (Command command : commands) {
            this.commands.values().stream().filter(commandInfo -> commandInfo.getCommand().getName().equals(command.getName()))
                    .collect(Collectors.toList()).forEach(commandInfo -> this.commands.values().remove(commandInfo));
        }
        return this;
    }

    @Override
    public CommandManager unregisterCommands(Addon addon) {
        this.commands.values().stream().filter(commandInfo -> commandInfo.getAddon() != null && commandInfo.getAddon().getAddonConfig().equals(addon.getAddonConfig())).collect(Collectors.toList())
                .forEach(commandInfo -> this.commands.values().remove(commandInfo));
        return this;
    }

    /**
     * Unregisters commands in this CommandManagerImpl
     * @param names the names of the commands to unregister
     * @return this
     */
    public CommandManagerImpl unregisterCommands(String... names) {
        for (String name : names) {
            CommandInfo info = this.commands.remove(name.toLowerCase());
            if (info == null)
                continue;
            Command command = info.getCommand();
            if (command.getAliases() != null && command.getAliases().length != 0) {
                for (String alias : command.getAliases()) {
                    this.commands.remove(alias.toLowerCase());
                }
            }
        }
        return this;
    }

    /**
     * Dispatches a command with the given {@link CommandSender} if they exist
     * @param commandSender the sender to dispatch with
     * @param commandLine the string from which we get the name of the command and the arguments
     * @return true if the command exists and was dispatched, false if the command does not exist
     */
    public boolean dispatchCommand(CommandSender commandSender, String commandLine) {
        String[] a = commandLine.split(" ");
        if (a.length == 0)
            return false;
        String commandName = a[0].toLowerCase();
        CommandInfo command = this.commands.get(commandName);
        if (command == null)
            return false;

        command.getCommand().execute(commandSender, commandLine, Arrays.copyOfRange(a, 1, a.length));
        return true;
    }

    public Command getCommand(String name) {
        CommandInfo info = this.commands.get(name.toLowerCase());
        return info == null ? null : info.getCommand();
    }

    public Command getCommandByLine(String commandLine) {
        String[] a = commandLine.split(" ");
        if (a.length == 0)
            return null;
        return this.getCommand(a[0]);
    }

    public Map<String, Command> getCommands() {
        Map<String, Command> commandMap = new HashMap<>();
        for (CommandInfo value : this.commands.values()) {
            commandMap.put(value.getCommand().getName(), value.getCommand());
        }
        return commandMap;
    }

    public void unregisterAll() {
        this.commands.clear();
    }

    public CommandSender getConsole() {
        return console;
    }
}
