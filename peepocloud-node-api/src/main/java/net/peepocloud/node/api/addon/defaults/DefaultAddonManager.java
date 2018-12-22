package net.peepocloud.node.api.addon.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import net.md_5.bungee.http.HttpClient;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.addon.node.NodeAddon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Consumer;

public abstract class DefaultAddonManager {

    public abstract void getDefaultAddons(Consumer<Collection<DefaultAddonConfig>> consumer);

    public abstract void getDefaultAddon(String name, Consumer<DefaultAddonConfig> consumer);

    public abstract InstallAddonResult installAddon(DefaultAddonConfig defaultAddonConfig);

    public abstract boolean uninstallAddon(DefaultAddonConfig defaultAddonConfig);

    public abstract UpdateAddonResult updateAddon(DefaultAddonConfig defaultAddonConfig);

    @AllArgsConstructor
    public static enum UpdateAddonResult {

        ADDON_NOT_FOUND("addons.defaults.update.notInstalled"),
        ADDON_UP_TO_DATE("addons.defaults.update.alreadyUpToDate"),
        DOWNLOAD_FAILED("addons.defaults.update.downloadFailed"),
        ADDON_ALREADY_INSTALLED("addons.defaults.update.alreadyInstalled"),
        ADDON_LOAD_FAILED("addons.defaults.update.loadFailed"),
        SUCCESS("addons.defaults.update.success");

        private String key;

        public String formatMessage(DefaultAddonConfig addonConfig) {
            String message = PeepoCloudNodeAPI.getInstance().getLanguagesManager().getMessage(key);

            if (this == DOWNLOAD_FAILED) {
                return String.format(message, addonConfig.getName(), addonConfig.getVersion());
            }
            return message;
        }

    }

    @AllArgsConstructor
    public static enum InstallAddonResult {

        ADDON_ALREADY_INSTALLED("addons.defaults.install.alreadyInstalled"),
        DOWNLOAD_FAILED("addons.defaults.install.downloadFailed"),
        ADDON_LOAD_FAILED("addons.defaults.install.loadFailed"),
        SUCCESS("addons.defaults.install.success");

        private String key;

        public String formatMessage(DefaultAddonConfig addonConfig) {
            String message = PeepoCloudNodeAPI.getInstance().getLanguagesManager().getMessage(key);

            if (this == DOWNLOAD_FAILED) {
                return String.format(message, addonConfig.getName(), addonConfig.getVersion());
            }
            return message;
        }

    }

}
