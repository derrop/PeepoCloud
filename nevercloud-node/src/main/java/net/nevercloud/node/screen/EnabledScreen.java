package net.nevercloud.node.screen;
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

    public abstract void write(String line);
}
