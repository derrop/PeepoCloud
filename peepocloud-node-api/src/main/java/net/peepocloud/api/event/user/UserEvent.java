package net.peepocloud.api.event.user;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.api.event.Event;
import net.peepocloud.api.users.User;

@AllArgsConstructor
@Getter
public class UserEvent extends Event {
    private User user;
}
