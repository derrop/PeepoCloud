package net.peepocloud.addons.serverselector.listener;

import net.peepocloud.addons.serverselector.ServerSelectorAddon;
import net.peepocloud.addons.serverselector.packet.PacketOutAPISignSelector;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.node.api.event.EventHandler;
import net.peepocloud.node.api.event.network.minecraftserver.ServerConnectEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ServerStartListener {
    private ServerSelectorAddon serverSelectorAddon;

    public ServerStartListener(ServerSelectorAddon serverSelectorAddon) {
        this.serverSelectorAddon = serverSelectorAddon;
    }

    @EventHandler
    public void handleServerStart(ServerConnectEvent event) {
        SimpleJsonObject signSelectorContainer = this.serverSelectorAddon.getSignSelectorContainer();
        Collection<ServerSign> serverSigns = Arrays.stream(signSelectorContainer
                .getObject("serverSigns", ServerSign[].class)).filter(serverSign ->
                serverSign.getPosition().getSavedOnGroup().equalsIgnoreCase(event.getServerInfo().getGroupName())).collect(Collectors.toList());

        event.getParticipant().sendPacket(new PacketOutAPISignSelector(signSelectorContainer
                .append("serverSigns", serverSigns)
                .append("config", this.serverSelectorAddon.getSignSelectorConfig())));
    }


}
