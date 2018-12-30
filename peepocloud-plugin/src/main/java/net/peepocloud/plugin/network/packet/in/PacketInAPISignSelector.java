package net.peepocloud.plugin.network.packet.in;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.serverselector.signselector.AnimatedSignLayout;
import net.peepocloud.lib.serverselector.signselector.SignLayout;
import net.peepocloud.lib.serverselector.signselector.SignSelectorConfig;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignSelector;

import java.util.function.Consumer;

public class PacketInAPISignSelector extends JsonPacketHandler {


    @Override
    public int getId() {
        return 150;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if(PeepoCloudPlugin.getInstance().isBukkit()) {
            SimpleJsonObject signSelectorContainer = packet.getSimpleJsonObject();
            SignSelector signSelector = new SignSelector(signSelectorContainer.getObject("config", SignSelectorConfig.class),
                    signSelectorContainer.getObject("serverSigns", ServerSign[].class), signSelectorContainer.getObject("signLayouts", SignLayout[].class),
                    signSelectorContainer.getObject("loadingLayout", AnimatedSignLayout.class), signSelectorContainer.getObject("maintenanceLayout", AnimatedSignLayout.class));
            PeepoCloudPlugin.getInstance().toBukkit().setupSignSelector(signSelector);
        }
    }
}
