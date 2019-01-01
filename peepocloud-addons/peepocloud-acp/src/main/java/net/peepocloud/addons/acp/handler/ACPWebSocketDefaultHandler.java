package net.peepocloud.addons.acp.handler;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import com.google.gson.JsonElement;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.users.User;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.ClientNode;
import net.peepocloud.node.api.websocket.server.JsonServerWebSocketHandler;
import net.peepocloud.node.api.websocket.server.WebSocket;

import java.lang.management.ManagementFactory;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ACPWebSocketDefaultHandler extends JsonServerWebSocketHandler {
    @Override
    public void handleJson(WebSocket webSocket, SimpleJsonObject jsonObject, Consumer<SimpleJsonObject> consumer) throws Exception {
        String key = jsonObject.getString("key");
        SimpleJsonObject data = jsonObject.getJsonObject("data");
        JsonElement unique = jsonObject.get("unique");

        Consumer<SimpleJsonObject> responseConsumer = jsonObject1 -> {
            jsonObject1.append("unique", unique);
            consumer.accept(jsonObject1);
        };

        switch (key) {

            case "statistics":
            {
                responseConsumer.accept(new SimpleJsonObject().append("statistics", new SimpleJsonObject().append("...", "...").asJsonObject()));
            }
            break;

            case "users":
            {
                PeepoCloudNode.getInstance().getUserManager().getUsers(users -> responseConsumer.accept(new SimpleJsonObject().append("users", users)));
            }
            break;

            case "minecraftgroups":
            {
                responseConsumer.accept(new SimpleJsonObject().append("groups", PeepoCloudNode.getInstance().getMinecraftGroups().values()));
            }
            break;

            case "bungeegroups":
            {
                responseConsumer.accept(new SimpleJsonObject().append("groups", PeepoCloudNode.getInstance().getBungeeGroups().values()));
            }
            break;

            case "cpuUsage":
            {
                responseConsumer.accept(new SimpleJsonObject().append("cpuUsage", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
            }
            break;

            case "memory":
            {
                responseConsumer.accept(new SimpleJsonObject().append("usage", PeepoCloudNode.getInstance().getMemoryUsed()).append("max", PeepoCloudNode.getInstance().getMaxMemory()));
            }
            break;

            case "players":
            {
                responseConsumer.accept(new SimpleJsonObject().append("online", 0).append("max", 0)); //TODO
            }
            break;

            case "nodes":
            {
                responseConsumer.accept(
                        new SimpleJsonObject().append("connected", PeepoCloudNode.getInstance().getConnectedNodes().values().stream()
                                .map(ClientNode::getNodeInfo).collect(Collectors.toList())
                        )
                );
            }
            break;

            case "servers":
            {
                responseConsumer.accept(new SimpleJsonObject().append("servers", PeepoCloudNode.getInstance().getMinecraftServers()));
            }
            break;

            case "proxies":
            {
                responseConsumer.accept(new SimpleJsonObject().append("proxies", PeepoCloudNode.getInstance().getBungeeProxies()));
            }
            break;

            case "deleteUser":
            {
                if (data == null)
                    break;

                String username = data.getString("username");
                if (username == null)
                    break;
                PeepoCloudNode.getInstance().getUserManager().removeUser(username);
            }
            break;

            case "createUser":
            {
                if (data == null) {
                    consumer.accept(new SimpleJsonObject().append("success", false));
                    break;
                }

                String username = data.getString("username");
                String password = data.getString("password");
                String iconUrl = data.getString("iconUrl");
                if (username == null || password == null) {
                    consumer.accept(new SimpleJsonObject().append("success", false));
                    break;
                }

                User user = new User(username, password, SystemUtils.randomString(32));
                if (iconUrl != null) {
                    user.getMetaData().append("iconUrl", iconUrl);
                }
                PeepoCloudNode.getInstance().getUserManager().addUser(user);

            }
            break;

            case "changeUserPassword":
            {
                if (data == null) {
                    consumer.accept(new SimpleJsonObject().append("success", false));
                    break;
                }

                String username = data.getString("username");
                String password = data.getString("password");
                if (username == null || password == null) {
                    consumer.accept(new SimpleJsonObject().append("success", false));
                    break;
                }

                PeepoCloudNode.getInstance().getUserManager().getUser(username, user -> {
                    user.setHashedPassword(SystemUtils.hashString(password));
                    PeepoCloudNode.getInstance().getUserManager().updateUser(user);
                    consumer.accept(new SimpleJsonObject().append("success", true));
                });
            }
            break;

            case "changeUserToken":
            {
                if (data == null) {
                    consumer.accept(new SimpleJsonObject().append("success", false));
                    break;
                }

                String username = data.getString("username");
                String password = data.getString("apiToken");
                if (username == null || password == null) {
                    consumer.accept(new SimpleJsonObject().append("success", false));
                    break;
                }

                PeepoCloudNode.getInstance().getUserManager().getUser(username, user -> {
                    user.setApiToken(password);
                    PeepoCloudNode.getInstance().getUserManager().updateUser(user);
                    consumer.accept(new SimpleJsonObject().append("success", true));
                });
            }
            break;

        }

    }
}
