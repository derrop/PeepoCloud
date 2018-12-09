package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.logging.ColoredLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final CommandSender console = new ConsoleCommandSender();

    private Map<String, Command> commands = new HashMap<>();
    private Thread commandReaderThread;

    public CommandManager(ColoredLogger logger) {
        logger.getConsoleReader().addCompleter(new CommandCompleter(this));
        this.commandReaderThread = new Thread("ConsoleCommand Reader") {
            @Override
            public void run() {
                try {
                    String line;
                    while (!isInterrupted() && PeepoCloudNode.getInstance().isRunning() && (line = logger.readLine1()) != null) {
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
        this.commandReaderThread.start();
    }

    public void shutdown() {
        this.commandReaderThread.interrupt();
    }

    /**
     * Registers commands in this CommandManager by their name and aliases
     * @param commands the commands to register
     * @return this
     */
    public CommandManager registerCommands(Command... commands) {
        for (Command command : commands) {
            this.commands.put(command.getName().toLowerCase(), command);
            if (command.getAliases() != null && command.getAliases().length != 0) {
                for (String alias : command.getAliases()) {
                    this.commands.put(alias.toLowerCase(), command);
                }
            }
        }
        return this;
    }

    /**
     * Unregisters commands in this CommandManager
     * @param commands the commands to unregister
     * @return this
     */
    public CommandManager unregisterCommands(Command... commands) {
        for (Command command : commands) {
            this.commands.values().remove(command);
        }
        return this;
    }

    /**
     * Unregisters commands in this CommandManager
     * @param names the names of the commands to unregister
     * @return this
     */
    public CommandManager unregisterCommands(String... names) {
        for (String name : names) {
            Command command = this.commands.remove(name.toLowerCase());
            if (command != null && command.getAliases() != null && command.getAliases().length != 0) {
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
        Command command = this.commands.get(commandName);
        if (command == null)
            return false;

        command.execute(commandSender, commandLine, Arrays.copyOfRange(a, 1, a.length));
        return true;
    }

    /**
     * Gets the command by the given name
     * @param name the name of the command
     * @return the command or null if no command with this name exists
     */
    public Command getCommand(String name) {
        return this.commands.get(name.toLowerCase());
    }

    /**
     * Gets the command by the given commandLine
     * @param commandLine the commandLine from which we get the name of the command
     * @return the command or null if no command with this name exists
     */
    public Command getCommandByLine(String commandLine) {
        String[] a = commandLine.split(" ");
        if (a.length == 0)
            return null;
        String commandName = a[0].toLowerCase();
        return this.commands.get(commandName);
    }

    /**
     * Gets all registered commands with the key as their name/alias
     * @return the commands registered in this {@link CommandManager}
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Gets the default {@link ConsoleCommandSender}
     * @return the {@link ConsoleCommandSender} for this {@link CommandManager}
     */
    public CommandSender getConsole() {
        return console;
    }
}