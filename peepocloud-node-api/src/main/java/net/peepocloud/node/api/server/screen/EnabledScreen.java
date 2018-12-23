package net.peepocloud.node.api.server.screen;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class EnabledScreen {
    private String componentName;
    private UUID uniqueId;

    /**
     * Dispatches the given command to this screen
     *
     * @param line the command to dispatch
     */
    public abstract void write(String line);
}
