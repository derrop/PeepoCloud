package net.peepocloud.node;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import com.google.gson.JsonElement;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.out.group.PacketOutCreateBungeeGroup;
import net.peepocloud.lib.network.packet.out.group.PacketOutCreateMinecraftGroup;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.node.api.database.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GroupsConfig {

    private Map<String, MinecraftGroup> minecraftGroups;
    private Map<String, BungeeGroup> bungeeGroups;

    public Map<String, MinecraftGroup> loadMinecraftGroups() {
        this.minecraftGroups = new HashMap<>();
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("minecraftGroups");
        database.forEach(simpleJsonObject -> {
            MinecraftGroup group = SimpleJsonObject.GSON.get().fromJson(simpleJsonObject.asJsonObject(), MinecraftGroup.class);
            if (group != null) {
                this.loadGroup(group);
            }
        });

        return this.minecraftGroups;
    }

    public Map<String, BungeeGroup> loadBungeeGroups() {
        this.bungeeGroups = new HashMap<>();
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("bungeeGroups");
        database.forEach(simpleJsonObject -> {
            BungeeGroup group = SimpleJsonObject.GSON.get().fromJson(simpleJsonObject.asJsonObject(), BungeeGroup.class);
            if (group != null) {
                this.loadGroup(group);
            }
        });
        return this.bungeeGroups;
    }

    public void createGroup(BungeeGroup group, Consumer<Boolean> success) {
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("bungeeGroups");
        doCreate(aBoolean -> {
            if (aBoolean) {
                this.loadGroup(group);
                PeepoCloudNode.getInstance().sendPacketToNodes(new PacketOutCreateBungeeGroup(group));
            }
            success.accept(aBoolean);
        }, database, group.getName(), SimpleJsonObject.GSON.get().toJsonTree(group));
    }

    public void createGroup(MinecraftGroup group, Consumer<Boolean> success) {
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("minecraftGroups");
        doCreate(aBoolean -> {
            if (aBoolean) {
                this.loadGroup(group);
                PeepoCloudNode.getInstance().sendPacketToNodes(new PacketOutCreateMinecraftGroup(group));
            }
            success.accept(aBoolean);
        }, database, group.getName(), SimpleJsonObject.GSON.get().toJsonTree(group));
    }

    public void deleteMinecraftGroup(String name) {
        PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("minecraftGroups").deleteAsync(name);
    }

    public void deleteBungeeGroup(String name) {
        PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("bungeeGroups").deleteAsync(name);
    }

    public void update(MinecraftGroup group) {
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("minecraftGroups");
        database.updateAsync(group.getName(), new SimpleJsonObject(group));
    }

    public void update(BungeeGroup group) {
        Database database = PeepoCloudNode.getInstance().getDatabaseManager().getDatabase("bungeeGroups");
        database.updateAsync(group.getName(), new SimpleJsonObject(group));
    }

    private void doCreate(Consumer<Boolean> success, Database database, String name, JsonElement jsonElement) {
        database.contains(name).thenAccept(aBoolean -> {
            if (aBoolean) {
                success.accept(false);
            } else {
                database.insertAsync(name, new SimpleJsonObject(jsonElement.getAsJsonObject()));
                success.accept(true);
            }
        });
    }

    private void loadGroup(BungeeGroup group) {
        System.out.println("&aLoading BungeeGroup &e" + group.getName() + "&a...");
        this.bungeeGroups.put(group.getName(), group);
    }

    private void loadGroup(MinecraftGroup group) {
        System.out.println("&aLoading MinecraftGroup &e" + group.getName() + "&a...");
        this.minecraftGroups.put(group.getName(), group);
    }

}
