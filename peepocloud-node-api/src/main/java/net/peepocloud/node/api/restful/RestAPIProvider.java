package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import net.peepocloud.node.api.addon.Addon;
import net.peepocloud.node.api.restful.handler.RestAPIHandler;

import java.net.InetSocketAddress;

public interface RestAPIProvider {

    boolean isRunning();

    boolean isRateLimitEnabled();

    InetSocketAddress getHostAddress();

    void registerAPIHandler(Addon addon, RestAPIHandler handler);

    void unregisterAPIHandlers(Addon addon);

}
