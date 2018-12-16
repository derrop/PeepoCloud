package net.peepocloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.api.server.Template;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
/**
 * Called when a process starts up and the template will tried to be copied, in the inputStream must be a zipped directory (the template)
 */
public class BungeeCordTemplateCopyEvent extends ProcessEvent {
    private BungeeCordProxyInfo proxyInfo;
    @Setter
    private InputStream inputStream;

    public BungeeCordTemplateCopyEvent(CloudProcess cloudProcess, BungeeCordProxyInfo proxyInfo, InputStream inputStream) {
        super(cloudProcess);
        this.proxyInfo = proxyInfo;
        this.inputStream = inputStream;
    }

    public Template getTemplate() {
        return this.proxyInfo.getTemplate();
    }
}
