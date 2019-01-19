package net.peepocloud.node.server.minecraftserverfile;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.MapBuilder;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.server.ServerVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class MinecraftServerStartupFileVersion {

    private static final Collection<MinecraftServerStartupFileVersion> DEFAULTS = Arrays.asList(
            new MinecraftServerStartupFileVersion("Spigot",
                    mapVersions(
                            new ServerVersion("1.13.2", "https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar"),
                            new ServerVersion("1.13.1", "https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar"),
                            new ServerVersion("1.13", "https://cdn.getbukkit.org/spigot/spigot-1.13.jar"),
                            new ServerVersion("1.12.2", "https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar"),
                            new ServerVersion("1.12.1", "https://cdn.getbukkit.org/spigot/spigot-1.12.1.jar"),
                            new ServerVersion("1.12", "https://cdn.getbukkit.org/spigot/spigot-1.12.jar"),
                            new ServerVersion("1.11.2", "https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar"),
                            new ServerVersion("1.11.1", "https://cdn.getbukkit.org/spigot/spigot-1.11.1.jar"),
                            new ServerVersion("1.11", "https://cdn.getbukkit.org/spigot/spigot-1.11.jar"),
                            new ServerVersion("1.10.2", "https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.10", "https://cdn.getbukkit.org/spigot/spigot-1.10-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.9.4", "https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.9.2", "https://cdn.getbukkit.org/spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.9", "https://cdn.getbukkit.org/spigot/spigot-1.9-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8.8", "https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8.7", "https://cdn.getbukkit.org/spigot/spigot-1.8.7-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8.6", "https://cdn.getbukkit.org/spigot/spigot-1.8.6-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8.5", "https://cdn.getbukkit.org/spigot/spigot-1.8.5-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8.4", "https://cdn.getbukkit.org/spigot/spigot-1.8.4-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8.3", "https://cdn.getbukkit.org/spigot/spigot-1.8.3-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.8", "https://cdn.getbukkit.org/spigot/spigot-1.8-R0.1-SNAPSHOT-latest.jar"),
                            new ServerVersion("1.7.10", "https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar"),
                            new ServerVersion("1.7.9", "https://cdn.getbukkit.org/spigot/spigot-1.7.9-R0.2-SNAPSHOT.jar"),
                            new ServerVersion("1.7.8", "https://cdn.getbukkit.org/spigot/spigot-1.7.8-R0.1-SNAPSHOT.jar"),
                            new ServerVersion("1.7.5", "https://cdn.getbukkit.org/spigot/spigot-1.7.5-R0.1-SNAPSHOT-1387.jar"),
                            new ServerVersion("1.7.2", "https://cdn.getbukkit.org/spigot/spigot-1.7.2-R0.4-SNAPSHOT-1339.jar"),
                            new ServerVersion("1.6.4", "https://cdn.getbukkit.org/spigot/spigot-1.6.4-R2.1-SNAPSHOT.jar"),
                            new ServerVersion("1.6.2", "https://cdn.getbukkit.org/spigot/spigot-1.6.2-R1.1-SNAPSHOT.jar"),
                            new ServerVersion("1.5.2", "https://cdn.getbukkit.org/spigot/spigot-1.5.2-R1.1-SNAPSHOT.jar"),
                            new ServerVersion("1.5.1", "https://cdn.getbukkit.org/spigot/spigot-1.5.1-R0.1-SNAPSHOT.jar"),
                            new ServerVersion("1.4.7", "https://cdn.getbukkit.org/spigot/spigot-1.4.7-R1.1-SNAPSHOT.jar"),
                            new ServerVersion("1.4.6", "https://cdn.getbukkit.org/spigot/spigot-1.4.6-R0.4-SNAPSHOT.jar")
                    )
            )
    );

    private String name;
    private Map<String, ServerVersion> versions;

    @Override
    public String toString() {
        return name;
    }

    public ServerVersion getVersion(String name) {
        return this.versions.get(name);
    }

    private static Map<String, ServerVersion> mapVersions(ServerVersion... versions) {
        return Arrays.stream(versions).collect(Collectors.toMap(ServerVersion::getVersion, o -> o));
    }

    public static String asString(Collection<MinecraftServerStartupFileVersion> versions) {
        return versions.stream().map(minecraftServerStartupFileVersion -> minecraftServerStartupFileVersion.name).collect(Collectors.joining(", "));
    }

    public static Collection<MinecraftServerStartupFileVersion> getAvailableVersions() {
        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "spigotVersions").openConnection();
            connection.setConnectTimeout(1000);
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                Map<String, MinecraftServerStartupFileVersion> versionMap = SimpleJsonObject.GSON.get().fromJson(reader, new TypeToken<Map<String, MinecraftServerStartupFileVersion>>() {
                }.getType());
                return versionMap.values();
            }
        } catch (IOException e) {
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("startupfiles.spigot.noConnection"));
        }
        return DEFAULTS;
    }
}
