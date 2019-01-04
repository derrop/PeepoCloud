package net.peepocloud.lib.utility.network;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import net.peepocloud.lib.network.packet.Packet;

import java.util.function.Function;

public class FunctionalQueryRequest<T> extends QueryRequest<T> {

    private Function<Packet, T> function;

    public FunctionalQueryRequest(Function<Packet, T> function) {
        this.function = function;
    }

    public void setResponse(Packet packet) {
        super.setResponse(function.apply(packet));
    }
}
