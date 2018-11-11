package net.nevercloud.node.network.packet.serverside.auth;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.AllArgsConstructor;
import net.nevercloud.lib.json.SimpleJsonObject;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.handler.PacketHandler;
import net.nevercloud.node.network.NetworkServer;

import java.util.function.Consumer;
public class PacketInAuth extends JsonPacket {
    public PacketInAuth(int id) {
        super(id);
    }

    @AllArgsConstructor
    public static class NetworkAuthHandler implements PacketHandler {

        private NetworkServer networkServer;

        @Override
        public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
            if (!(packet instanceof PacketInAuth)) {
                networkParticipant.getChannel().close();
                return;
            }
            SimpleJsonObject jsonObject = ((PacketInAuth) packet).getSimpleJsonObject();
            Auth auth = SimpleJsonObject.GSON.fromJson(jsonObject.asJsonObject(), Auth.class);
            if (auth == null) {
                networkParticipant.getChannel().close();
                return;
            }
            if (auth.getAuthKey() == null || auth.getComponentName() == null || auth.getType() == null) {
                networkParticipant.getChannel().close();
                return;
            }

            this.networkServer.handleAuth(networkParticipant, auth);

        }
    }

}
