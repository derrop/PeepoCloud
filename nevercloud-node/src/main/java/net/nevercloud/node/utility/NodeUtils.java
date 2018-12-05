package net.nevercloud.node.utility;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import lombok.AllArgsConstructor;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class NodeUtils {
    private NodeUtils() { }

    public static void updateNodeInfoForSupport(Consumer<Boolean> consumer) {
        if (!NeverCloudNode.getInstance().getInternalConfig().getBoolean("acceptedInformationsSendingToServer")) {
            consumer.accept(null);
            return;
        }
        NeverCloudNode.getInstance().getUniqueId(uniqueId -> {
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
                            new CloudInfo(
                                    uniqueId,
                                    SystemUtils.getOperatingSystem(),
                                    SystemUtils.getCurrentVersion(),
                                    NeverCloudNode.getInstance().getMaxMemory(),
                                    SystemUtils.getAvailableCpuCores()
                            ),
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
        });
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
