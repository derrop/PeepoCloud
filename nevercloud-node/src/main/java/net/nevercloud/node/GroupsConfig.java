package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import com.google.gson.JsonElement;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.server.bungee.BungeeGroup;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;
import net.nevercloud.node.database.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GroupsConfig {

    private Map<String, MinecraftGroup> minecraftGroups;
    private Map<String, BungeeGroup> bungeeGroups;

    public Map<String, MinecraftGroup> loadMinecraftGroups() {
        Map<String, MinecraftGroup> groups = new HashMap<>();
        Database database = NeverCloudNode.getInstance().getDatabaseManager().getDatabase("minecraftGroups");
        database.forEach(simpleJsonObject -> {
            MinecraftGroup group = SimpleJsonObject.GSON.fromJson(simpleJsonObject.asJsonObject(), MinecraftGroup.class);
            if (group != null) {
                groups.put(group.getName(), group);
            }
        });
        this.minecraftGroups = groups;
        return groups;
    }

    public Map<String, BungeeGroup> loadBungeeGroups() {
        Map<String, BungeeGroup> groups = new HashMap<>();
        Database database = NeverCloudNode.getInstance().getDatabaseManager().getDatabase("bungeeGroups");
        database.forEach(simpleJsonObject -> {
            BungeeGroup group = SimpleJsonObject.GSON.fromJson(simpleJsonObject.asJsonObject(), BungeeGroup.class);
            if (group != null) {
                groups.put(group.getName(), group);
            }
        });
        this.bungeeGroups = groups;
        return groups;
    }

    public void createGroup(BungeeGroup group, Consumer<Boolean> success) {
        Database database = NeverCloudNode.getInstance().getDatabaseManager().getDatabase("bungeeGroups");
        doCreate(aBoolean -> {
            if (aBoolean)
                this.bungeeGroups.put(group.getName(), group);
            success.accept(aBoolean);
        }, database, group.getName(), SimpleJsonObject.GSON.toJsonTree(group));
    }

    public void createGroup(MinecraftGroup group, Consumer<Boolean> success) {
        Database database = NeverCloudNode.getInstance().getDatabaseManager().getDatabase("minecraftGroups");
        doCreate(aBoolean -> {
            if (aBoolean)
                this.minecraftGroups.put(group.getName(), group);
            success.accept(aBoolean);
        }, database, group.getName(), SimpleJsonObject.GSON.toJsonTree(group));
    }

    private void doCreate(Consumer<Boolean> success, Database database, String name, JsonElement jsonElement) {
        database.contains(name, aBoolean -> {
            if (aBoolean) {
                success.accept(false);
            } else {
                database.insert(name, new SimpleJsonObject(jsonElement.getAsJsonObject()));
                success.accept(true);
            }
        });
    }

}
