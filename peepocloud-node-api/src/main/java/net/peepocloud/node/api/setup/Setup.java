package net.peepocloud.node.api.setup;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.peepocloud.lib.config.Configurable;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.logging.ConsoleLogger;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class Setup {

    protected static SetupFactory factory;

    /**
     * Starts a setup asynchronously
     * @param configurable the configurable to which the user input is saved
     * @param logger the logger in which the setup is made
     * @param consumer the consumer which will be accepted with the new setup asynchronously
     */
    public static void startSetupAsync(Configurable configurable, ConsoleLogger logger, Consumer<Setup> consumer) {
        PeepoCloudNodeAPI.getInstance().getExecutorService().execute(() -> {
            consumer.accept(factory.createSetup(configurable, logger));
        });
    }

    /**
     * Starts a setup synchronously
     * @param configurable the configurable to which the user input is saved
     * @param logger the logger in which the setup is made
     * @param consumer the consumer which will be accepted with the new setup synchronously
     */
    public static void startSetupSync(Configurable configurable, ConsoleLogger logger, Consumer<Setup> consumer) {
        consumer.accept(factory.createSetup(configurable, logger));
    }

    /**
     * Defines if the user can type "cancel" to cancel the setup, default is false
     * @param cancellable if the user can type "cancel" to cancel the setup {@code true} or {@code false} if not
     * @return this
     */
    public abstract Setup setCancellable(boolean cancellable);

    public abstract Setup request(String name, String requestMessage, String invalidInputMessage, SetupAcceptable setupAcceptable, Collection<String> available);

    /**
     * Gets the {@link Configurable} which was specified to this Setup with all the user data
     * @return the {@link Configurable} of this setup
     */
    public abstract Configurable getData();

    protected static abstract class SetupFactory {
        public abstract Setup createSetup(Configurable configurable, ConsoleLogger logger);
    }

}
