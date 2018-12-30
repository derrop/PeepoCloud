package net.peepocloud.addons.serverselector.listener;

import net.peepocloud.addons.serverselector.ServerSelectorAddon;
import net.peepocloud.addons.serverselector.packet.PacketOutAPISignSelector;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.api.event.EventHandler;
import net.peepocloud.node.api.event.network.minecraftserver.ServerConnectEvent;

public class ServerStartListener {
    private ServerSelectorAddon serverSelectorAddon;

    public ServerStartListener(ServerSelectorAddon serverSelectorAddon) {
        this.serverSelectorAddon = serverSelectorAddon;
    }

    @EventHandler
    public void handleServerStart(ServerConnectEvent event) {
        SimpleJsonObject signSelectorContainer = this.serverSelectorAddon.getSignSelectorContainer();
        event.getParticipant().sendPacket(new PacketOutAPISignSelector(signSelectorContainer
                .append("serverSigns", this.serverSelectorAddon.getSignsFromGroup(event.getServerInfo().getGroupName()))
                .append("config", this.serverSelectorAddon.getSignSelectorConfig())));
    }


}
