package net.peepocloud.plugin.bukkit.command.subcommand.signselector;


import net.peepocloud.lib.serverselector.Position;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bukkit.command.subcommand.SubCommandExecutor;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignProvider;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class RemoveSignSubCommand extends SubCommandExecutor {
    private SignProvider signProvider;

    public RemoveSignSubCommand(SignProvider signProvider) {
        super("removeSign","Removes a serverSign on the sign you look at", "removeSign");
        this.signProvider = signProvider;
    }

    @Override
    public CommandExecutor subExecutor() {
        return (sender, command, label, args) -> {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length == 0) {
                    Block targetBlock = player.getTargetBlock(null, 20);
                    if (targetBlock != null && (targetBlock.getType() == Material.WALL_SIGN
                            || targetBlock.getType() == Material.SIGN_POST || targetBlock.getType() == Material.SIGN)) {
                        Position position = this.signProvider.fromBukkitLocation(targetBlock.getLocation(),
                                PeepoCloudPlugin.getInstance().toBukkit().getCurrentServerInfo().getGroupName());
                        if(this.signProvider.getByPosition(position) != null) {
                            this.signProvider.removeSign(position);
                            player.sendMessage("§7The serverSign was successfully removed!");
                        } else
                            player.sendMessage("§7This sign is not a serverSign!");
                    } else
                        player.sendMessage("§7You have to look at a sign!");
                } else
                    player.sendMessage("§7Usage: §e/cloudplugin " + super.getUsage());
            }
            return true;
        };
    }

}
