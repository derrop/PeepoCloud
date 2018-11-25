package net.nevercloud.node.api.events.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.lib.server.Template;
import net.nevercloud.node.api.events.internal.Event;

import java.io.InputStream;

@Getter
@AllArgsConstructor
/**
 * Called when a server starts up and the template will tried to be copied, in the inputStream must be a zipped directory (the template)
 */
public class MinecraftServerTemplateCopyEvent extends Event {
    private MinecraftServerInfo serverInfo;
    @Setter
    private InputStream inputStream;

    public Template getTemplate() {
        return this.serverInfo.getTemplate();
    }
}
