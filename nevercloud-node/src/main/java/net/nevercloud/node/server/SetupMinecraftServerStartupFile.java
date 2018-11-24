package net.nevercloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.config.yaml.YamlConfigurable;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.setup.ArraySetupAcceptable;
import net.nevercloud.node.setup.EnumSetupAcceptable;
import net.nevercloud.node.setup.Setup;
import net.nevercloud.node.utility.FileDownloading;

import java.nio.file.Path;

public class SetupMinecraftServerStartupFile {

    public static void installServer(ColoredLogger logger, Path path) {
        Setup.startSetupSync(new YamlConfigurable(), logger, setup -> {
            setup.request(
                    "type",
                    "Please specify a minecraft server version [" + MinecraftServerStartupFileVersion.asString() + "]",
                    "You have to specify one of the following: " + MinecraftServerStartupFileVersion.asString(),
                    new EnumSetupAcceptable(MinecraftServerStartupFileVersion.class)
            );
            String type = setup.getData().getString("type");
            String url = null;
            for (MinecraftServerStartupFileVersion startupFileVersion : MinecraftServerStartupFileVersion.values()) {
                if (type.equalsIgnoreCase(startupFileVersion.name())) {
                    requestVersions(setup, startupFileVersion.getVersions());
                    url = startupFileVersion.getVersion(setup.getData().getString("version")).getUrl();
                    break;
                }
            }

            String finalUrl = url;
            FileDownloading.downloadFileWithProgressBar(logger, url, path, () -> {
                        System.out.println("&aSuccessfully downloaded server");
                    },
                    () -> {
                        System.out.println("&cThere was an error while downloading server.jar from " + finalUrl);
                    });
            /*System.out.println("&aDownloading server...");
            ConsoleProgressBarAnimation animation = new ConsoleProgressBarAnimation(logger, 0, 0, '=', '>', "<!", "!> %value% bytes / %length% bytes | %percent%, %time%, %bps%");
            Consumer<Integer> consumer = animation::setCurrentValue;
            int length = SystemUtils.downloadFile(url, path, consumer, () -> {
                System.out.println("&aSuccessfully downloaded server");
            });
            if (length != -1) {
                animation.setLength(length);
                logger.startAnimation(animation);
            } else {
                System.out.println("&cThere was an error while downloading server.jar from " + url);
            }*/
        });
    }

    private static void requestVersions(Setup setup, ServerVersion[] serverVersions) {
        StringBuilder invalid = new StringBuilder();
        for (ServerVersion version : serverVersions) {
            invalid.append(version.getName()).append(", ");
        }
        setup.request(
                "version",
                "Please specify the version [" + invalid.substring(0, invalid.length() - 2) + "]",
                "You have to specify one of the following versions: " + invalid.substring(0, invalid.length() - 2),
                new ArraySetupAcceptable<>(serverVersions));
    }

}
