package net.peepocloud.node.addon;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.common.base.Preconditions;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.peepocloud.node.PeepoCloudNode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonManager<Addon extends net.peepocloud.node.addon.Addon> {

    private Map<String, Addon> loadedAddons = new HashMap<>();

    public void loadAddons(String directory) throws IOException {
        this.loadAddons(directory, null);
    }

    public void loadAddons(Path directory) throws IOException {
        this.loadAddons(directory, null);
    }

    public void loadAddons(String directory, Consumer<Addon> preLoadAddon) throws IOException {
        this.loadAddons(Paths.get(directory), preLoadAddon);
    }

    public Collection<Addon> loadAddons(Path directory, Consumer<Addon> preLoadAddon) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            return Collections.emptyList();
        }

        Collection<Path> paths = new ArrayList<>();

        Files.walkFileTree(
                directory,
                EnumSet.noneOf(FileVisitOption.class),
                Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (!Files.isDirectory(file) && file.toString().endsWith(".jar")) {
                            paths.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                }
        );

        Collection<Addon> addons = new ArrayList<>();

        for (Path path : paths) {
            Addon addon = loadAddon(path, preLoadAddon);
            if (addon != null) {
                addons.add(addon);
            }
        }

        return addons;
    }

    public Addon loadAddon(Path path, Consumer<Addon> preLoadAddon) throws MalformedURLException {
        AddonLoader addonLoader = new AddonLoader(path);

        try (JarFile jarFile = new JarFile(path.toFile())) {
            JarEntry jarEntry = jarFile.getJarEntry("addon.yml");
            Preconditions.checkArgument(jarEntry != null, "addon " + path.toString() + " does not contain an addon.yml");

            try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8)) {
                Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
                AddonConfig config = new AddonConfig(
                        configuration.getString("name"),
                        configuration.getString("version"),
                        configuration.getString("author"),
                        configuration.getString("main"),
                        path.getFileName().toString(),
                        configuration.getString("website"),
                        configuration.contains("reloadType") ? AddonConfig.ReloadType.valueOf(configuration.getString("reloadType")) : AddonConfig.ReloadType.ALWAYS
                );
                if (this.loadedAddons.containsKey(config.getName()))
                    return this.loadedAddons.get(config.getName());

                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.loadingAddon")
                        .replace("%name%", config.getName()).replace("%author%", config.getAuthor()).replace("%version%", config.getVersion()));
                long start = System.nanoTime();
                Addon addon = (Addon) addonLoader.loadAddon(config);
                if (addon != null) {
                    addon.setConfigFile(Paths.get(path.getParent().toString(), config.getName() + "/config.yml"));
                    this.loadedAddons.put(config.getName(), addon);
                    if (preLoadAddon != null)
                        preLoadAddon.accept(addon);
                    addon.onLoad();
                    System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.successfullyLoadedAddon")
                            .replace("%name%", config.getName()).replace("%author%", config.getAuthor()).replace("%version%", config.getVersion())
                            .replace("%time%", String.valueOf(System.nanoTime() - start)));
                    return addon;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean loadAndEnableAddon(Path path) {
        try {
            Addon addon = this.loadAddon(path, null);
            if (addon != null) {
                this.enableAddon(addon);
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void enableAddons() {
        this.loadedAddons.values().forEach(this::enableAddon);
    }

    public void disableAndUnloadAddons() {
        this.disableAndUnloadAddons0(addon -> AddonConfig.ReloadType.ALWAYS.equals(addon.getAddonConfig().getReloadType()));
    }

    private void disableAndUnloadAddons0(Function<Addon, Boolean> mayDisableAndUnload) {
        new ArrayList<>(this.loadedAddons.values()).forEach(addon -> {
            if (mayDisableAndUnload.apply(addon)) {
                this.unloadAddon(addon);
            }
        });
    }

    public void disableAddon(Addon addon) {
        if (!addon.enabled)
            return;
        long start = System.nanoTime();
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.disablingAddon")
                .replace("%name%", addon.getAddonConfig().getName()).replace("%author%", addon.getAddonConfig().getAuthor())
                .replace("%version%", addon.getAddonConfig().getVersion()));
        addon.enabled = false;
        addon.onDisable();
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.disabledAddon")
                .replace("%name%", addon.getAddonConfig().getName()).replace("%author%", addon.getAddonConfig().getAuthor())
                .replace("%version%", addon.getAddonConfig().getVersion())
                .replace("%time%", String.valueOf(System.nanoTime() - start)));
    }

    public void unloadAddon(Addon addon) {
        if (!addon.enabled)
            return;
        this.loadedAddons.values().remove(addon);
        this.disableAddon(addon);
        try {
            addon.getAddonLoader().getClassLoader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableAddon(Addon addon) {
        if (addon.enabled)
            return;
        long start = System.nanoTime();
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.enablingAddon")
                .replace("%name%", addon.getAddonConfig().getName()).replace("%author%", addon.getAddonConfig().getAuthor())
                .replace("%version%", addon.getAddonConfig().getVersion()));
        addon.enabled = true;
        addon.onEnable();
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.enabledAddon")
                .replace("%name%", addon.getAddonConfig().getName()).replace("%author%", addon.getAddonConfig().getAuthor())
                .replace("%version%", addon.getAddonConfig().getVersion()).replace("%time%", String.valueOf(System.nanoTime() - start)));
    }

    public Addon getAddonByName(String name) {
        return this.loadedAddons.get(name);
    }

    public Addon getAddonByFileName(String fileName) {
        for (Addon addon : this.loadedAddons.values()) {
            if (addon.getAddonConfig().getFileName().equalsIgnoreCase(fileName)) {
                return addon;
            }
        }
        return null;
    }

    public void shutdown() {
        this.disableAndUnloadAddons0(addon -> true);
    }

    public Map<String, Addon> getLoadedAddons() {
        return Collections.unmodifiableMap(loadedAddons);
    }
}
