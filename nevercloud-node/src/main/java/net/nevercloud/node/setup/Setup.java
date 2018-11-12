package net.nevercloud.node.setup;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.nevercloud.lib.conf.IConfigurable;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.logging.ColoredLogger;

import java.util.function.Consumer;

public class Setup {

    private ColoredLogger logger;
    private IConfigurable configurable;

    private Setup(IConfigurable configurable, ColoredLogger logger) {
        this.logger = logger;
        this.configurable = configurable;
    }

    public static void startSetup(IConfigurable configurable, ColoredLogger logger, Consumer<Setup> consumer) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            consumer.accept(new Setup(configurable, logger));
        });
    }

    public Setup request(String name, String invalidInputMessage, SetupAcceptable setupAcceptable) {
        Object val;
        if (setupAcceptable instanceof BooleanSetupAcceptable) {
            val = Boolean.parseBoolean(
                    this.logger.readLineUntil(
                            s ->
                                    (s.equalsIgnoreCase("true") ||
                                            s.equalsIgnoreCase("false")) &&
                                            ((BooleanSetupAcceptable) setupAcceptable).onPrint(Boolean.parseBoolean(s)),
                            invalidInputMessage
                    )
            );
        } else if (setupAcceptable instanceof IntegerSetupAcceptable) {
            val = Integer.parseInt(
                    this.logger.readLineUntil(
                            s ->
                                    SystemUtils.isInteger(s) &&
                                            ((IntegerSetupAcceptable) setupAcceptable).onPrint(Integer.parseInt(s))
                            , invalidInputMessage
                    )
            );
        } else if (setupAcceptable instanceof StringSetupAcceptable) {
            val = this.logger.readLineUntil(
                    ((StringSetupAcceptable) setupAcceptable)::onPrint,
                    invalidInputMessage
            );
        } else {
            throw new IllegalArgumentException("setupAcceptable must be an instance of BooleanSetupAcceptable, IntegerSetupAcceptable or StringSetupAcceptable");
        }
        this.configurable.append(name, val);
        return this;
    }

    public IConfigurable getData() {
        return this.configurable;
    }

}
