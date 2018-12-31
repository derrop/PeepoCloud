package net.peepocloud.node.setup;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import lombok.Getter;
import net.peepocloud.lib.config.Configurable;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.logging.ColoredLogger;
import net.peepocloud.node.setup.type.BooleanSetupAcceptable;
import net.peepocloud.node.setup.type.IntegerSetupAcceptable;
import net.peepocloud.node.setup.type.StringSetupAcceptable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class Setup {

    private ColoredLogger logger;
    private Configurable configurable;
    private boolean cancellable = false;
    private boolean cancelled = false;
    @Getter
    private Collection<String> currentAvailable;

    public Setup(Configurable configurable, ColoredLogger logger) {
        this.logger = logger;
        this.configurable = configurable;
        logger.setRunningSetup(this);
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

    public Setup request(String name, String requestMessage, String invalidInputMessage, SetupAcceptable setupAcceptable, Collection<String> available) {
        if (this.cancelled)
            return this;
        this.currentAvailable = available;
        System.out.println(requestMessage);
        String response;
        Object val = null;
        if (setupAcceptable instanceof BooleanSetupAcceptable) {
            response = this.readUntil(
                    s ->
                            (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("no") ||
                                    s.equalsIgnoreCase("false")) &&
                                    ((BooleanSetupAcceptable) setupAcceptable).onPrint(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes")),
                    invalidInputMessage
            );
            if (response != null) {
                val = Boolean.parseBoolean(
                        response
                );
            }
        } else if (setupAcceptable instanceof IntegerSetupAcceptable) {
            response = this.readUntil(
                    s ->
                            SystemUtils.isInteger(s) &&
                                    ((IntegerSetupAcceptable) setupAcceptable).onPrint(Integer.parseInt(s)),
                    invalidInputMessage
            );
            if (response != null) {
                val = Integer.parseInt(
                        response
                );
            }
        } else if (setupAcceptable instanceof StringSetupAcceptable) {
            response = this.readUntil(
                    ((StringSetupAcceptable) setupAcceptable)::onPrint,
                    invalidInputMessage
            );
            if (response != null) {
                val = response;
            }
        } else {
            throw new IllegalArgumentException("setupAcceptable must be an instance of BooleanSetupAcceptable, IntegerSetupAcceptable or StringSetupAcceptable");
        }
        this.currentAvailable = null;
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

    private String readUntil(Function<String, Boolean> function, String invalidInputMessage) {
        return this.logger.readLineUntil(s -> function.apply(s.trim()), invalidInputMessage, this.cancellable ? "cancel" : null).trim();
    }

    /**
     * Gets the {@link Configurable} which was specified to this Setup with all the user data
     * @return the {@link Configurable} of this setup
     */
    public Configurable getData() {
        return this.configurable;
    }

}
