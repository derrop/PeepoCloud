package net.peepocloud.addons.serverselector.listener;


import net.peepocloud.addons.serverselector.ServerSelectorAddon;
import net.peepocloud.addons.serverselector.packet.PacketOutAPISignSelector;
import net.peepocloud.node.api.event.EventHandler;
import net.peepocloud.node.api.event.network.minecraftserver.ServerConnectEvent;

public class ServerStartListener {
    private ServerSelectorAddon serverSelectorAddon;

    public ServerStartListener(ServerSelectorAddon serverSelectorAddon) {
        this.serverSelectorAddon = serverSelectorAddon;
    }

    @EventHandler
    public void handleServerStart(ServerConnectEvent event) {
        event.getParticipant().sendPacket(new PacketOutAPISignSelector(this.serverSelectorAddon
                .getSignSelectorContainer().append("config", this.serverSelectorAddon.getSignSelectorConfig())));
    }


}
