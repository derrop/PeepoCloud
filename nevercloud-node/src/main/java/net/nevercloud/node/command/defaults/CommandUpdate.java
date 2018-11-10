package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import io.netty.util.internal.PlatformDependent;
import lombok.*;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandUpdate extends Command {
    public CommandUpdate() {
        super("update");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().getAutoUpdaterManager().checkUpdates(updateCheckResponse -> {
            if (updateCheckResponse != null) {
                if (updateCheckResponse.isUpToDate()) {
                    sender.sendMessage("&aYou are using the newest version of the System");
                } else {
                    sender.sendMessage("&eYou are &c" + updateCheckResponse.getVersionsBehind() + " &eversions behind, updating to &c" + updateCheckResponse.getNewestVersion() + "&e...");
                    NeverCloudNode.getInstance().getAutoUpdaterManager().update(success -> {
                        if (success) {
                            sender.sendMessage("&aSuccessfully updated to &c" + updateCheckResponse.getNewestVersion());
                            if (PlatformDependent.isWindows()) {
                                sender.sendMessage("&eYou're on windows, please copy the new created Jar &b\"" + SystemUtils.getPathOfInternalJarFile().replaceFirst(".jar", "") + "-update-....jar\" &eto &b\"" + SystemUtils.getPathOfInternalJarFile() + "\"&e, the system will exit...");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            NeverCloudNode.getInstance().shutdown();
                        } else {
                            sender.sendMessage("&cCould not update to &e" + updateCheckResponse.getNewestVersion());
                        }
                    });
                }
            } else {
                sender.sendMessage("&cThere was an error while trying to check for updates");
            }
        });
    }
}
