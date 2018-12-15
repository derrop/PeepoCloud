package net.peepocloud.node.api.event.user;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.node.utility.users.User;

public class UserCreateEvent extends UserEvent {
    public UserCreateEvent(User user) {
        super(user);
    }
}
