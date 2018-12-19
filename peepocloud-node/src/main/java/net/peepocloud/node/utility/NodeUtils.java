package net.peepocloud.node.utility;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.commons.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class NodeUtils {
    private NodeUtils() { }

    public static void updateNodeInfoForSupport(Consumer<Boolean> consumer) {
        if (!PeepoCloudNode.getInstance().getInternalConfig().getBoolean("acceptedInformationsSendingToServer") ||
                PeepoCloudNode.getInstance().getCloudConfig().getUsername() == null ||
                PeepoCloudNode.getInstance().getCloudConfig().getApiToken() == null ||
                PeepoCloudNode.getInstance().getCloudConfig().getUniqueId() == null) {
            if (consumer != null)
                consumer.accept(null);
            return;
        }
        String uniqueId = PeepoCloudNode.getInstance().getUniqueId();
        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "cloudInfoUpdate").openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(15000);
            connection.connect();

            try (OutputStream outputStream = connection.getOutputStream();
                 Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                SimpleJsonObject.GSON.toJson(
                        new SimpleJsonObject()
                                .append("username", PeepoCloudNode.getInstance().getCloudConfig().getUsername())
                                .append("apiToken", PeepoCloudNode.getInstance().getCloudConfig().getApiToken())
                                .append("cloudInfo", new CloudInfo(
                                                uniqueId,
                                                SystemUtils.getOperatingSystem(),
                                                SystemUtils.getCurrentVersion(),
                                                PeepoCloudNode.getInstance().getMaxMemory(),
                                                SystemUtils.getAvailableCpuCores()
                                        )
                                )
                                .asJsonObject(),
                        writer
                );
            }

            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                SimpleJsonObject simpleJsonObject = new SimpleJsonObject(reader);
                if (!simpleJsonObject.getBoolean("success")) {
                    System.out.println("&cCould not update the node info to the server: " + simpleJsonObject);
                    if (consumer != null)
                        consumer.accept(false);
                } else {
                    if (consumer != null)
                        consumer.accept(true);
                }
            }
        } catch (IOException e) {
            if (consumer != null)
                consumer.accept(false);
            if (!SystemUtils.isServerOffline(e))
                e.printStackTrace();
        }
    }


    @AllArgsConstructor
    private static final class CloudInfo {
        private String uniqueId;
        private String os;
        private String cloudVersion;
        private int maxMemory;
        private int cpuCores;
    }


}
