package net.peepocloud.node.api.server.screen;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.server.CloudProcess;

import java.util.UUID;
import java.util.function.Consumer;

public interface ScreenManager {

    /**
     * Enables a screen for the given {@link BungeeCordProxyInfo} and posts the complete log live to the given {@link Consumer}
     * @param proxyInfo the proxyInfo from which we get the screen
     * @param consumer the consumer to which we post the log
     * @return the enabled screen containing the uniqueId of the screen and the name of the proxy to disable it
     */
    public EnabledScreen enableScreen(BungeeCordProxyInfo proxyInfo, Consumer<String> consumer);

    /**
     * Enables a screen for the given {@link MinecraftServerInfo} and posts the complete log live to the given {@link Consumer}
     * @param serverInfo the serverInfo from which we get the screen
     * @param consumer the consumer to which we post the log
     * @return the enabled screen containing the uniqueId of the screen and the name of the server to disable it
     */
    public EnabledScreen enableScreen(MinecraftServerInfo serverInfo, Consumer<String> consumer);

    /**
     * Disables a screen if its running
     * @param componentName the name of the component from which the screen should be disabled
     * @param uniqueId the uniqueId of the screen that should be disabled
     * @return {@code true} if it has been disabled successfully or {@code false} if it was not enabled
     */
    public boolean disableScreen(String componentName, UUID uniqueId);

    /**
     * Disables a screen if its running
     * @param enabledScreen the screen to disable
     * @return {@code true} if it has been disabled successfully or {@code false} if it was not enabled
     */
    public boolean disableScreen(EnabledScreen enabledScreen);

}
