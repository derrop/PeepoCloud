package net.peepocloud.lib.utility.network;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NetworkAddress {
    private String host;
    private int port;

    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }

    public InetSocketAddress toInetSocketAddress() {
        return this.host.equals("*") ? new InetSocketAddress(this.port) : new InetSocketAddress(this.host, this.port);
    }
}
