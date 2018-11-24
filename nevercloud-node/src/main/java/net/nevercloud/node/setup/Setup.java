package net.nevercloud.node.setup;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.nevercloud.lib.config.IConfigurable;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.logging.ColoredLogger;

import java.util.function.Consumer;

public class Setup {

    private ColoredLogger logger;
    private IConfigurable configurable;
    private boolean cancellable = false;
    private boolean cancelled = false;

    public Setup(IConfigurable configurable, ColoredLogger logger) {
        this.logger = logger;
        this.configurable = configurable;
    }

    public static void startSetupAsync(IConfigurable configurable, ColoredLogger logger, Consumer<Setup> consumer) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            consumer.accept(new Setup(configurable, logger));
        });
    }

    public static void startSetupSync(IConfigurable configurable, ColoredLogger logger, Consumer<Setup> consumer) {
        consumer.accept(new Setup(configurable, logger));
    }

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
            System.out.println("&cThe setup was cancelled");
            this.cancelled = true;
            return this;
        }
        if (val == null)
            return this;
        this.configurable.append(name, val);
        return this;
    }

    public IConfigurable getData() {
        return this.configurable;
    }

}
