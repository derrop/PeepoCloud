package net.peepocloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
/**
 * Called when the bungee.jar is copied into a new proxy, before it's starting up
 */
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
