package net.peepocloud.node.api.command;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import java.util.Map;

public interface CommandManager {

    /**
     * Registers commands in this {@link CommandManager} by their name and aliases
     * @param commands the commands to register
     * @return this
     */
    public CommandManager registerCommands(Command... commands);

    /**
     * Unregisters commands in this {@link CommandManager}
     * @param commands the commands to unregister
     * @return this
     */
    public CommandManager unregisterCommands(Command... commands);

    /**
     * Unregisters commands in this {@link CommandManager}
     * @param names the names of the commands to unregister
     * @return this
     */
    public CommandManager unregisterCommands(String... names);

    /**
     * Dispatches a command with the given {@link CommandSender} if they exist
     * @param commandSender the sender to dispatch with
     * @param commandLine the string from which we get the name of the command and the arguments
     * @return true if the command exists and was dispatched, false if the command does not exist
     */
    public boolean dispatchCommand(CommandSender commandSender, String commandLine);

    /**
     * Gets the command by the given name
     * @param name the name of the command
     * @return the command or null if no command with this name exists
     */
    public Command getCommand(String name);

    /**
     * Gets the command by the given commandLine
     * @param commandLine the commandLine from which we get the name of the command
     * @return the command or null if no command with this name exists
     */
    public Command getCommandByLine(String commandLine);

    /**
     * Gets all registered commands with the key as their name/alias
     * @return the commands registered in this {@link CommandManager}
     */
    public Map<String, Command> getCommands();

    /**
     * Gets the default {@link CommandSender}
     * @return the {@link CommandSender} for this {@link CommandManager}
     */
    public CommandSender getConsole();

}
