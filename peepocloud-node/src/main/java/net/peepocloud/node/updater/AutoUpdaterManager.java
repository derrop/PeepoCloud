package net.peepocloud.node.updater;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThreadLocalRandom;
import net.md_5.bungee.http.HttpClient;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.utility.FileDownloading;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AutoUpdaterManager {

    public void checkUpdates(Consumer<UpdateCheckResponse> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "updatecheck?version=" + SystemUtils.getCurrentVersion(), null, (s, throwable) -> {
            if (throwable != null) {
                consumer.accept(null);
                throwable.printStackTrace();
                return;
            }

            SimpleJsonObject jsonObject = new SimpleJsonObject(s);
            if (jsonObject.getBoolean("success")) {
                UpdateCheckResponse response = new UpdateCheckResponse(
                        jsonObject.getInt("versionsBehind"),
                        jsonObject.getString("newestVersion"),
                        jsonObject.getBoolean("upToDate")
                );
                consumer.accept(response);
            } else {
                consumer.accept(null);
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("autoupdate.checkFailed").replace("%reason%", jsonObject.getString("reason")));
            }
        });
    }

    public UpdateCheckResponse checkUpdatesSync() {
        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "updatecheck?version=" + SystemUtils.getCurrentVersion()).openConnection();
            connection.connect();

            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                SimpleJsonObject jsonObject = new SimpleJsonObject(reader);
                if (jsonObject.getBoolean("success")) {
                    return new UpdateCheckResponse(
                            jsonObject.getInt("versionsBehind"),
                            jsonObject.getString("newestVersion"),
                            jsonObject.getBoolean("upToDate")
                    );
                } else {
                    System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("autoupdate.checkFailed").replace("%reason%", jsonObject.getString("reason")));
                }
            };
        } catch (IOException e) {
            if (!SystemUtils.isServerOffline(e))
                e.printStackTrace();
        }
        return null;
    }

    public void update(BiConsumer<Boolean, Path> consumer) {
        if (PlatformDependent.isWindows()) {
            Path path = Paths.get(SystemUtils.getPathOfInternalJarFile().replaceFirst(".jar", "") + "-update-" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE) + ".jar");
            consumer.accept(
                    FileDownloading.downloadFileWithProgressBar(
                            PeepoCloudNode.getInstance().getLogger(),
                            SystemUtils.CENTRAL_SERVER_URL + "updatenode",
                            path,
                            () -> {
                            },
                            () -> {
                            }
                    ),
                    path
            );
        } else {
            consumer.accept(
                    FileDownloading.downloadFileWithProgressBar(
                            PeepoCloudNode.getInstance().getLogger(),
                            SystemUtils.CENTRAL_SERVER_URL + "updatenode",
                            Paths.get(SystemUtils.getPathOfInternalJarFile()),
                            () -> {
                            },
                            () -> {
                            }
                    ),
                    null
            );
        }

    }

}
