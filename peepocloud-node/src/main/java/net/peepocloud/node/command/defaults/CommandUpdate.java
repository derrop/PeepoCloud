package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.network.packet.out.node.PacketOutNodeUpdate;

public class CommandUpdate extends Command {
    public CommandUpdate() {
        super("update");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        PeepoCloudNode.getInstance().getNetworkManager().sendPacketToNodes(new PacketOutNodeUpdate());
        PeepoCloudNode.getInstance().installUpdates(sender);
    }
}
