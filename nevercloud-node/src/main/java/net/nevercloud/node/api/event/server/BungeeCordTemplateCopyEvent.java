package net.nevercloud.node.api.event.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.nevercloud.lib.server.Template;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
/**
 * Called when a server starts up and the template will tried to be copied, in the inputStream must be a zipped directory (the template)
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
