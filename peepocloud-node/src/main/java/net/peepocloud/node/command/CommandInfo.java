package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 27.12.2018
 */

import lombok.*;
import net.peepocloud.node.api.addon.Addon;
import net.peepocloud.node.api.command.Command;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CommandInfo {
    private Command command;
    private Addon addon;
}
