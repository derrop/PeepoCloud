package net.nevercloud.node.addon.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import net.md_5.bungee.http.HttpClient;
import net.nevercloud.lib.conf.json.SimpleJsonObject;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.addon.node.NodeAddon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Consumer;

public class DefaultAddonManager {

    public void getDefaultAddons(Consumer<Collection<DefaultAddonConfig>> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "addons", null, (s, throwable) -> {
            if (throwable != null) {
                consumer.accept(null);
                throwable.printStackTrace();
            } else {
                consumer.accept(SimpleJsonObject.GSON.fromJson(s, new TypeToken<Collection<DefaultAddonConfig>>() {
                }.getType()));
            }
        });
    }

    public void getDefaultAddon(String name, Consumer<DefaultAddonConfig> consumer) {
        getDefaultAddons(defaultAddonConfigs -> {
            for (DefaultAddonConfig defaultAddonConfig : defaultAddonConfigs) {
                if (defaultAddonConfig.getName().equalsIgnoreCase(name)) {
                    consumer.accept(defaultAddonConfig);
                    return;
                }
            }
            consumer.accept(null);
        });
    }

    public InstallAddonResult installAddon(DefaultAddonConfig defaultAddonConfig) {
        Path path = Paths.get("nodeAddons/" + defaultAddonConfig.getName() + "-" + defaultAddonConfig.getVersion() + ".jar");
        if (Files.exists(path))
            return InstallAddonResult.ADDON_ALREADY_INSTALLED;

        if (!HttpClient.downloadFile(SystemUtils.CENTRAL_SERVER_URL + "file?addonName=" + defaultAddonConfig.getName() + "&addonVersion=" + defaultAddonConfig.getVersion(), path))
            return InstallAddonResult.DOWNLOAD_FAILED;
        if (!NeverCloudNode.getInstance().getNodeAddonManager().loadAndEnableAddon(path))
            return InstallAddonResult.ADDON_LOAD_FAILED;
        return InstallAddonResult.SUCCESS;
    }

    public boolean uninstallAddon(DefaultAddonConfig defaultAddonConfig) {
        Path path = Paths.get("nodeAddons/" + defaultAddonConfig.getName() + "-" + defaultAddonConfig.getVersion() + ".jar");
        if (!Files.exists(path))
            return false;

        NodeAddon addon = NeverCloudNode.getInstance().getNodeAddonManager().getAddonByFileName(path.getFileName().toString());

        if (addon == null)
            return false;

        NeverCloudNode.getInstance().getNodeAddonManager().unloadAddon(addon);

        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public UpdateAddonResult updateAddon(DefaultAddonConfig defaultAddonConfig) {
        NodeAddon addon = NeverCloudNode.getInstance().getNodeAddonManager().getAddonByName(defaultAddonConfig.getName());
        if (addon == null)
            return UpdateAddonResult.ADDON_NOT_FOUND;
        if (addon.getAddonConfig().getVersion().equals(defaultAddonConfig.getVersion()))
            return UpdateAddonResult.ADDON_UP_TO_DATE;

        {
            NeverCloudNode.getInstance().getNodeAddonManager().unloadAddon(addon);
            Path path = Paths.get("nodeAddons/" + addon.getAddonConfig().getFileName());
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        {
            Path path = Paths.get("nodeAddons/" + defaultAddonConfig.getName() + "-" + defaultAddonConfig.getVersion() + ".jar");
            if (Files.exists(path))
                return UpdateAddonResult.ADDON_ALREADY_INSTALLED;

            if (!HttpClient.downloadFile(SystemUtils.CENTRAL_SERVER_URL + "file?addonName=" + defaultAddonConfig.getName() + "&addonVersion=" + defaultAddonConfig.getVersion(), path))
                return UpdateAddonResult.DOWNLOAD_FAILED;

            if (!NeverCloudNode.getInstance().getNodeAddonManager().loadAndEnableAddon(path))
                return UpdateAddonResult.ADDON_LOAD_FAILED;
        }

        return UpdateAddonResult.SUCCESS;
    }

    @AllArgsConstructor
    public static enum UpdateAddonResult {

        ADDON_NOT_FOUND("&cThe specified addon is not installed"),
        ADDON_UP_TO_DATE("&cThe specified addon is already on the newest version"),
        DOWNLOAD_FAILED("&cThe download of the addon %s-%s failed, please report this message to the support that we can fix this issue"),
        ADDON_ALREADY_INSTALLED("&cThe specified addon is already installed"),
        ADDON_LOAD_FAILED("&cAn error occurred while loading and enabling the addon"),
        SUCCESS("&aThe addon was successfully updated");

        private String message;

        public String formatMessage(DefaultAddonConfig addonConfig) {
            if (this == DOWNLOAD_FAILED) {
                return String.format(message, addonConfig.getName(), addonConfig.getVersion());
            }
            return message;
        }

    }

    @AllArgsConstructor
    public static enum InstallAddonResult {

        ADDON_ALREADY_INSTALLED("&cThe specified addon is already installed"),
        DOWNLOAD_FAILED("&cThe download of the addon %s-%s failed, please report this message to the support that we can fix this issue"),
        ADDON_LOAD_FAILED("&cAn error occurred while loading and enabling the addon"),
        SUCCESS("&aThe addon was successfully installed");

        private String message;

        public String formatMessage(DefaultAddonConfig addonConfig) {
            if (this == DOWNLOAD_FAILED) {
                return String.format(message, addonConfig.getName(), addonConfig.getVersion());
            }
            return message;
        }

    }

}
