package net.peepocloud.node.api.addon.node;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.node.api.addon.Addon;

public class NodeAddon extends Addon {
    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    public void initPacketHandlers(PacketManager packetManager) {}
}
