package net.nevercloud.node.server.minecraftserverfiles;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.server.ServerVersion;

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

@Getter
@AllArgsConstructor
public class MinecraftServerStartupFileVersion {

    private static final Collection<MinecraftServerStartupFileVersion> DEFAULTS = Arrays.asList(
            new MinecraftServerStartupFileVersion("Spigot", ImmutableMap.of("1.12.2", new ServerVersion("1.12.2", "https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar")))
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

    public static String asString(Collection<MinecraftServerStartupFileVersion> versions) {
        StringBuilder builder = new StringBuilder();
        for (MinecraftServerStartupFileVersion availableVersion : versions) {
            builder.append(availableVersion.name).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

    public static Collection<MinecraftServerStartupFileVersion> getAvailableVersions() {
        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "spigotVersions").openConnection();
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                Map<String, MinecraftServerStartupFileVersion> versionMap = SimpleJsonObject.GSON.fromJson(reader, new TypeToken<Map<String, MinecraftServerStartupFileVersion>>() {
                }.getType());
                return versionMap.values();
            }
        } catch (IOException e) {
            System.out.println("&cNo connection to the central server could be established, using default spigot versions...");
        }
        return DEFAULTS;
    }
}
