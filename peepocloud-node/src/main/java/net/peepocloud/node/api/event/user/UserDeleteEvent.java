package net.peepocloud.node.api.event.user;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.lib.users.User;

public class UserDeleteEvent extends UserEvent {
    public UserDeleteEvent(User user) {
        super(user);
    }
}
