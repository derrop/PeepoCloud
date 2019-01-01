package net.peepocloud.node.setup;
/*
 * Created by Mc_Ruben on 01.01.2019
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.lib.config.Configurable;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.logging.ConsoleLogger;
import net.peepocloud.node.api.setup.Setup;
import net.peepocloud.node.api.setup.SetupAcceptable;
import net.peepocloud.node.api.setup.type.BooleanSetupAcceptable;
import net.peepocloud.node.api.setup.type.IntegerSetupAcceptable;
import net.peepocloud.node.api.setup.type.StringSetupAcceptable;
import net.peepocloud.node.logging.ColoredLogger;

import java.util.Collection;
import java.util.function.Function;

public class SetupImpl extends Setup {

    static {
        factory = new SetupFactory() {
            @Override
            public Setup createSetup(Configurable configurable, ConsoleLogger logger) {
                Preconditions.checkArgument(logger.getClass().equals(ColoredLogger.class), "logger must be an instance of ColoredLogger");
                return new SetupImpl(configurable, logger);
            }
        };
    }

    private ColoredLogger logger;
    private Configurable configurable;
    private boolean cancellable = false;
    private boolean cancelled = false;
    @Getter
    private Collection<String> currentAvailable;

    public SetupImpl(Configurable configurable, ConsoleLogger logger) {
        this.configurable = configurable;
        this.logger = (ColoredLogger) logger;
        this.logger.setRunningSetup(this);
    }

    @Override
    public Setup setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    @Override
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
            System.out.println(PeepoCloudNodeAPI.getInstance().getLanguagesManager().getMessage("setup.cancelled"));
            this.cancelled = true;
            return this;
        }
        if (val == null)
            return this;
        this.configurable.append(name, val);
        return this;
    }

    @Override
    public Configurable getData() {
        return this.configurable;
    }

    private String readUntil(Function<String, Boolean> function, String invalidInputMessage) {
        return this.logger.readLineUntil(s -> function.apply(s.trim()), invalidInputMessage, this.cancellable ? "cancel" : null).trim();
    }

}
