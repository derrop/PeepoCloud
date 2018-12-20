package net.peepocloud.node.api.event.user;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.node.api.event.Event;
import net.peepocloud.lib.users.User;

@AllArgsConstructor
@Getter
public class UserEvent extends Event {
    private User user;
}
