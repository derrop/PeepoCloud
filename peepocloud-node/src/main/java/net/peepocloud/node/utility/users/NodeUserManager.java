package net.peepocloud.node.utility.users;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import com.google.gson.reflect.TypeToken;
import lombok.*;
import net.peepocloud.node.api.event.user.UserCreateEvent;
import net.peepocloud.node.api.event.user.UserDeleteEvent;
import net.peepocloud.node.api.event.user.UserUpdateEvent;
import net.peepocloud.lib.users.User;
import net.peepocloud.lib.users.UserManager;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.database.Database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

@Getter
public class NodeUserManager implements UserManager { //TODO send on user update/create/delete api packet to all network components

    public void getUsers(Consumer<Collection<User>> consumer) {
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("internal_configs");
        database.get("users", simpleJsonObject -> {
            if (simpleJsonObject == null) {
                database.insert("users", new SimpleJsonObject().append("users", Collections.emptyList()));
                consumer.accept(new ArrayList<>());
                return;
            }

            Collection<User> users = (Collection<User>) simpleJsonObject.getObject("users", new TypeToken<Collection<User>>() {
            }.getType());
            if (users == null)
                users = new ArrayList<>();
            consumer.accept(users);
        });
    }

    public void getUser(String username, Consumer<User> consumer) {
        this.getUsers(users -> {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    consumer.accept(user);
                    return;
                }
            }
            consumer.accept(null);
        });
    }

    private void save(Collection<User> users) {
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("internal_configs");
        database.update("users", new SimpleJsonObject().append("users", users));
    }

    public void addUser(User user) {
        this.getUsers(users -> {
            if (checkExists(users, user.getUsername()))
                return;
            users.add(user);
            this.save(users);
            PeepoCloudNode.getInstance().getEventManager().callEvent(new UserCreateEvent(user));
        });
    }

    public void updateUser(User newUser) {
        this.getUsers(users -> {
            User user = null;
            boolean a = false;
            for (User b : new ArrayList<>(users)) {
                if (b.getUsername().equals(newUser.getUsername())) {
                    user = b;
                    a = true;
                    break;
                }
            }
            if (a && users.remove(user)) {
                users.add(newUser);
                this.save(users);
                PeepoCloudNode.getInstance().getEventManager().callEvent(new UserUpdateEvent(user, newUser));
            }
        });
    }

    public void removeUser(User user) {
        this.removeUser(user.getUsername());
    }

    public void removeUser(String name) {
        this.getUsers(users -> {
            User user = null;
            boolean a = false;
            for (User b : new ArrayList<>(users)) {
                if (b.getUsername().equals(name)) {
                    user = b;
                    a = true;
                    break;
                }
            }
            if (a && users.remove(user)) {
                this.save(users);
                PeepoCloudNode.getInstance().getEventManager().callEvent(new UserDeleteEvent(user));
            }
        });
    }

    public void checkCredentials(String name, String password, Consumer<Boolean> consumer) {
        this.getUsers(users -> {
            String hashedPassword = SystemUtils.hashString(password);
            for (User user : users) {
                if (user.getUsername().equals(name) && user.getHashedPassword().equals(hashedPassword)) {
                    consumer.accept(true);
                    return;
                }
            }
            consumer.accept(false);
        });
    }

    private boolean checkExists(Collection<User> users, String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

}
