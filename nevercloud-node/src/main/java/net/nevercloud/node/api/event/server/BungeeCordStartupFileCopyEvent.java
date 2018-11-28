package net.nevercloud.node.api.event.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
public class BungeeCordStartupFileCopyEvent extends ProcessEvent {
    private BungeeCordProxyInfo proxyInfo;
    @Setter
    private InputStream inputStream;

    public BungeeCordStartupFileCopyEvent(CloudProcess cloudProcess, BungeeCordProxyInfo proxyInfo, InputStream inputStream) {
        super(cloudProcess);
        this.proxyInfo = proxyInfo;
        this.inputStream = inputStream;
    }
}
