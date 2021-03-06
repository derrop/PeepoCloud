package net.peepocloud.node.api.event.user;
/*
 * Created by Mc_Ruben on 11.12.2018
 */

import lombok.Getter;
import net.peepocloud.lib.users.User;

@Getter
public class UserUpdateEvent extends UserEvent {
    private User newUser;

    public UserUpdateEvent(User oldUser, User newUser) {
        super(oldUser);
        this.newUser = newUser;
    }

    public User getOldUser() {
        return super.getUser();
    }
}
