package net.peepocloud.node.api.event.user;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.node.api.event.internal.Event;
import net.peepocloud.node.utility.users.User;

@AllArgsConstructor
@Getter
public class UserEvent extends Event {
    private User user;
}
