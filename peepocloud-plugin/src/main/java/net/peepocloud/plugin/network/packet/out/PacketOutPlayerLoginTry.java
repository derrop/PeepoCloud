package net.peepocloud.plugin.network.packet.out;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.player.PeepoPlayer;

public class PacketOutPlayerLoginTry extends SerializationPacket {
    public PacketOutPlayerLoginTry(PeepoPlayer player) {
        super(60, player);
    }

    public PacketOutPlayerLoginTry(int id) {
        super(id);
    }
}
