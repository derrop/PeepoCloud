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

import java.io.InputStream;

@Getter
@AllArgsConstructor
/**
 * Called when a server starts up and the template will tried to be copied, in the inputStream must be a zipped directory (the template)
 */
public class BungeeCordTemplateCopyEvent extends Event {
    private BungeeCordProxyInfo proxyInfo;
    @Setter
    private InputStream inputStream;

    public Template getTemplate() {
        return this.proxyInfo.getTemplate();
    }
}
