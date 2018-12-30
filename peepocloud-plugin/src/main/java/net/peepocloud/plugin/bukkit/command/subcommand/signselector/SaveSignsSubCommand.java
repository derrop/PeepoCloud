package net.peepocloud.plugin.bukkit.command.subcommand.signselector;


import net.peepocloud.plugin.bukkit.command.subcommand.SubCommandExecutor;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignProvider;
import org.bukkit.command.CommandExecutor;

public class SaveSignsSubCommand extends SubCommandExecutor {
    private SignProvider signProvider;

    public SaveSignsSubCommand(SignProvider signProvider) {
        super("saveSigns", "Saves the signs which the signSelector provides currently into the database", "saveSigns");
        this.signProvider = signProvider;
    }

    @Override
    public CommandExecutor subExecutor() {
        return (sender, command, label, args) -> {
            this.signProvider.save();
            sender.sendMessage("The signs were sent to the node.");
            return true;
        };
    }
}
