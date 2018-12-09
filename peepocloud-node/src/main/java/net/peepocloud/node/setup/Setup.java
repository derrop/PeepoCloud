package net.peepocloud.node.setup;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.peepocloud.lib.config.Configurable;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.logging.ColoredLogger;
import net.peepocloud.node.setup.type.BooleanSetupAcceptable;
import net.peepocloud.node.setup.type.IntegerSetupAcceptable;
import net.peepocloud.node.setup.type.StringSetupAcceptable;

import java.util.function.Consumer;

public class Setup {

    private ColoredLogger logger;
    private Configurable configurable;
    private boolean cancellable = false;
    private boolean cancelled = false;

    public Setup(Configurable configurable, ColoredLogger logger) {
        this.logger = logger;
        this.configurable = configurable;
    }

    /**
     * Starts a setup asynchronously
     * @param configurable the configurable to which the user input is saved
     * @param logger the logger in which the setup is made
     * @param consumer the consumer which will be accepted with the new setup asynchronously
     */
    public static void startSetupAsync(Configurable configurable, ColoredLogger logger, Consumer<Setup> consumer) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            consumer.accept(new Setup(configurable, logger));
        });
    }

    /**
     * Starts a setup synchronously
     * @param configurable the configurable to which the user input is saved
     * @param logger the logger in which the setup is made
     * @param consumer the consumer which will be accepted with the new setup synchronously
     */
    public static void startSetupSync(Configurable configurable, ColoredLogger logger, Consumer<Setup> consumer) {
        consumer.accept(new Setup(configurable, logger));
    }

    /**
     * Defines if the user can type "cancel" to cancel the setup, default is false
     * @param cancellable if the user can type "cancel" to cancel the setup {@code true} or {@code false} if not
     * @return this
     */
    public Setup setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    public Setup request(String name, String requestMessage, String invalidInputMessage, SetupAcceptable setupAcceptable) {
        if (this.cancelled)
            return this;
        System.out.println(requestMessage);
        String response;
        Object val = null;
        if (setupAcceptable instanceof BooleanSetupAcceptable) {
            response = this.logger.readLineUntil(
                    s ->
                            (s.equalsIgnoreCase("true") ||
                                    s.equalsIgnoreCase("false")) &&
                                    ((BooleanSetupAcceptable) setupAcceptable).onPrint(Boolean.parseBoolean(s)),
                    invalidInputMessage,
                    cancellable ? "cancel" : null
            );
            if (response != null) {
                val = Boolean.parseBoolean(
                        response
                );
            }
        } else if (setupAcceptable instanceof IntegerSetupAcceptable) {
            response = this.logger.readLineUntil(
                    s ->
                            SystemUtils.isInteger(s) &&
                                    ((IntegerSetupAcceptable) setupAcceptable).onPrint(Integer.parseInt(s)),
                    invalidInputMessage,
                    cancellable ? "cancel" : null
            );
            if (response != null) {
                val = Integer.parseInt(
                        response
                );
            }
        } else if (setupAcceptable instanceof StringSetupAcceptable) {
            response = this.logger.readLineUntil(
                    ((StringSetupAcceptable) setupAcceptable)::onPrint,
                    invalidInputMessage,
                    cancellable ? "cancel" : null
            );
            if (response != null) {
                val = response;
            }
        } else {
            throw new IllegalArgumentException("setupAcceptable must be an instance of BooleanSetupAcceptable, IntegerSetupAcceptable or StringSetupAcceptable");
        }
        if (response == null && this.cancellable) {
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("setup.cancelled"));
            this.cancelled = true;
            return this;
        }
        if (val == null)
            return this;
        this.configurable.append(name, val);
        return this;
    }

    /**
     * Gets the {@link Configurable} which was specified to this Setup with all the user data
     * @return the {@link Configurable} of this setup
     */
    public Configurable getData() {
        return this.configurable;
    }

}
