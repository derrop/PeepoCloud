package net.peepocloud.node.server.bungeefile;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.node.logging.ColoredLogger;
import net.peepocloud.node.server.ServerVersion;
import net.peepocloud.node.api.setup.type.ArraySetupAcceptable;
import net.peepocloud.node.api.setup.Setup;
import net.peepocloud.node.api.utility.FileDownloading;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class SetupBungeeStartupFile { //TODO implement languagesystem

    public static void installBungee(ColoredLogger logger, Path path) {
        Setup.startSetupSync(new YamlConfigurable(), logger, setup -> {
            Collection<BungeeStartupFileVersion> fileVersions = BungeeStartupFileVersion.getAvailableVersions();
            String versions = BungeeStartupFileVersion.asString(fileVersions);
            setup.request(
                    "type",
                    "Please specify a bungee version [" + versions + "]",
                    "You have to specify one of the following: " + versions,
                    new ArraySetupAcceptable<>(fileVersions.toArray()),
                    fileVersions.stream().map(BungeeStartupFileVersion::getName).collect(Collectors.toList())
            );
            String type = setup.getData().getString("type");
            String url = null;
            for (BungeeStartupFileVersion startupFileVersion : fileVersions) {
                if (type.equalsIgnoreCase(startupFileVersion.getName())) {
                    requestVersions(setup, startupFileVersion.getVersions().values());
                    url = startupFileVersion.getVersion(setup.getData().getString("version")).getUrl();
                    break;
                }
            }

            String finalUrl = url;
            FileDownloading.downloadFileWithProgressBar(logger, url, path, () -> System.out.println("&aSuccessfully downloaded bungee"),
                    () -> System.out.println("&cThere was an error while downloading bungee.jar from " + finalUrl));
        });
    }

    private static void requestVersions(Setup setup, Collection<ServerVersion> serverVersions) {
        String s = serverVersions.stream().map(ServerVersion::getVersion).collect(Collectors.joining(", "));
        setup.request(
                "version",
                "Please specify the version [" + s + "]",
                "You have to specify one of the following versions: " + s,
                new ArraySetupAcceptable<>(serverVersions.toArray()),
                serverVersions.stream().map(ServerVersion::getVersion).collect(Collectors.toList())
        );
    }

}
