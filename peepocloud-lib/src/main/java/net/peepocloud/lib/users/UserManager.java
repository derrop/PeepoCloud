package net.peepocloud.api.users;
/*
 * Created by Mc_Ruben on 17.12.2018
 */

import java.util.Collection;
import java.util.function.Consumer;

public interface UserManager {

    void getUsers(Consumer<Collection<User>> consumer);

    void getUser(String username, Consumer<User> consumer);

    void addUser(User user);

    void updateUser(User user);

    void removeUser(User user);

    void removeUser(String username);

    void checkCredentials(String name, String password, Consumer<Boolean> consumer);

}
