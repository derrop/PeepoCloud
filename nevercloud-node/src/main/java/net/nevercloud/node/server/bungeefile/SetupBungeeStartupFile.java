package net.nevercloud.node.server.bungeefile;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.config.yaml.YamlConfigurable;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.server.ServerVersion;
import net.nevercloud.node.setup.type.ArraySetupAcceptable;
import net.nevercloud.node.setup.Setup;
import net.nevercloud.node.utility.FileDownloading;

import java.nio.file.Path;
import java.util.Collection;

public class SetupBungeeStartupFile { //TODO implement languagesystem

    public static void installBungee(ColoredLogger logger, Path path) {
        Setup.startSetupSync(new YamlConfigurable(), logger, setup -> {
            Collection<BungeeStartupFileVersion> fileVersions = BungeeStartupFileVersion.getAvailableVersions();
            String versions = BungeeStartupFileVersion.asString(fileVersions);
            setup.request(
                    "type",
                    "Please specify a bungee version [" + versions + "]",
                    "You have to specify one of the following: " + versions,
                    new ArraySetupAcceptable<>(fileVersions.toArray())
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
        StringBuilder invalid = new StringBuilder();
        for (ServerVersion version : serverVersions) {
            invalid.append(version.getVersion()).append(", ");
        }
        setup.request(
                "version",
                "Please specify the version [" + invalid.substring(0, invalid.length() - 2) + "]",
                "You have to specify one of the following versions: " + invalid.substring(0, invalid.length() - 2),
                new ArraySetupAcceptable<>(serverVersions.toArray()));
    }

}
