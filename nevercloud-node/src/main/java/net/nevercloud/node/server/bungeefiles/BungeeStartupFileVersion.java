package net.nevercloud.node.server.bungeefiles;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
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
public class BungeeStartupFileVersion {

    private static final Collection<BungeeStartupFileVersion> DEFAULTS = Arrays.asList(
            new BungeeStartupFileVersion("Bungee", ImmutableMap.of("latest", new ServerVersion("latest", "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar")))
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

    public static String asString(Collection<BungeeStartupFileVersion> versions) {
        StringBuilder builder = new StringBuilder();
        for (BungeeStartupFileVersion availableVersion : versions) {
            builder.append(availableVersion.name).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

    public static Collection<BungeeStartupFileVersion> getAvailableVersions() {
        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "bungeeVersions").openConnection();
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                Map<String, BungeeStartupFileVersion> versionMap = SimpleJsonObject.GSON.fromJson(reader, new TypeToken<Map<String, BungeeStartupFileVersion>>() {
                }.getType());
                return versionMap.values();
            }
        } catch (IOException e) {
            System.out.println(NeverCloudNode.getInstance().getLanguagesManager().getMessage("startupfiles.bungee.noConnection"));
        }
        return DEFAULTS;
    }
}
