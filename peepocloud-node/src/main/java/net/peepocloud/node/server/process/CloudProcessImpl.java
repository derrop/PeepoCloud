package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.node.api.server.CloudProcess;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface CloudProcessImpl extends CloudProcess {

    Map<UUID, Consumer<String>> getScreenHandlers();

    Consumer<String> getNetworkScreenHandler();

    void setNetworkScreenHandler(Consumer<String> consumer);

}
