package net.peepocloud.addons.templates.ftp;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.peepocloud.addons.templates.ftp.network.packet.out.PacketFtpOutTemplateUpdated;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.installableplugins.InstallablePlugin;
import net.peepocloud.node.api.server.TemplateStorage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RequiredArgsConstructor
public class FtpTemplateStorage extends TemplateStorage {

    private final FtpTemplatesAddon addon;
    @Getter
    private String cacheDir = "internal/cache/ftp/";

    private String getTemplateDir() {
        return this.getTemplateDir() + "templates";
    }

    private String getGlobalDir() {
        return this.getTemplateDir() + "global";
    }

    private String getPluginsDir() {
        return this.getTemplateDir() + "plugins";
    }

    public FtpClient getFtpClient() {
        return this.addon.getFtpClient();
    }

    @Override
    public String getName() {
        return "ftp";
    }

    @Override
    public boolean isWorking() {
        return this.getFtpClient() != null && this.getFtpClient().isConnected();
    }

    @Override
    public void copyToPath(MinecraftGroup group, Template template, Path target) {
        this.copyToPath(group.getName(), template, target);
    }

    @Override
    public void copyToPath(BungeeGroup group, Template template, Path target) {
        this.copyToPath(group.getName(), template, target);
    }

    private void copyToPath(String group, Template template, Path target) {
        String path = group + "/" + template.getName();
        Path cache = this.updateCache0(path);

        SystemUtils.copyDirectory(cache, target.toString());
    }

    @Override
    public void copyToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template) {
        this.copyToTemplate(directory, serverInfo.getGroupName(), template.getName());
    }

    @Override
    public void copyToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template) {
        this.copyToTemplate(directory, proxyInfo.getGroupName(), template.getName());
    }

    private void copyToTemplate(Path directory, String group, String template) {
        this.getFtpClient().uploadDirectory(directory, this.getTemplateDir() + group + "/" + template);
        SystemUtils.copyDirectory(directory, this.cacheDir + group + "/" + template);
        this.sendTemplateUpdate(group, template);
    }

    @Override
    public void copyFilesToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template, String[] files) {
        this.copyFilesToTemplate(directory, serverInfo.getGroupName(), template.getName(), files);
    }

    @Override
    public void copyFilesToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template, String[] files) {
        this.copyFilesToTemplate(directory, proxyInfo.getGroupName(), template.getName(), files);
    }

    private void copyFilesToTemplate(Path directory, String group, String template, String[] files) {
        boolean a = false;
        String prefix = this.getTemplateDir() + group + "/" + template + "/";
        for (String file : files) {
            Path path = Paths.get(directory.toString(), file);
            if (!Files.exists(path))
                continue;
            if (Files.isDirectory(path)) {
                this.getFtpClient().uploadDirectory(path, prefix + file);
            } else {
                this.getFtpClient().uploadFile(path, prefix + file);
            }
            a = true;
        }
        if (a) {
            this.sendTemplateUpdate(group, template);
        }
    }

    @Override
    public void copyStreamToTemplate(MinecraftGroup group, Template template, InputStream inputStream, String path) {
        copyStreamToTemplate0(template, inputStream, path, group.getName());
    }

    @Override
    public void copyStreamToTemplate(BungeeGroup group, Template template, InputStream inputStream, String path) {
        copyStreamToTemplate0(template, inputStream, path, group.getName());
    }

    private void copyStreamToTemplate0(Template template, InputStream inputStream, String path, String name) {
        String fullPath = name + "/" + template + "/" + path;

        Path cache = Paths.get(this.cacheDir + fullPath);
        try {
            Files.copy(inputStream, cache, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getFtpClient().uploadFile(cache, this.getTemplateDir() + fullPath);
    }

    @Override
    public void deleteTemplate(MinecraftGroup group, Template template) {
        this.deleteTemplate(group.getName(), template);
    }

    @Override
    public void deleteTemplate(BungeeGroup group, Template template) {
        this.deleteTemplate(group.getName(), template);
    }

    private void deleteTemplate(String group, Template template) {
        String path = group + "/" + template.getName();
        SystemUtils.deleteDirectory(Paths.get(this.cacheDir + path));
        this.getFtpClient().deleteDirectory(this.getTemplateDir() + path);
    }

    @Override
    public void createTemplate(MinecraftGroup group, Template template) {
        this.createTemplate(group.getName(), template);
    }

    @Override
    public void createTemplate(BungeeGroup group, Template template) {
        this.createTemplate(group.getName(), template);
    }

    private void createTemplate(String group, Template template) {
        String path = this.getTemplateDir() + group + "/" + template.getName();
        if (!this.getFtpClient().existsDirectory(path))
            this.getFtpClient().createDirectories(path);
    }

    @Override
    public void copyGlobal(Path target) {
        String remote = this.getGlobalDir();
        if (!this.getFtpClient().existsDirectory(remote)) {
            this.getFtpClient().createDirectories(remote);
            return;
        }
        String cache = this.cacheDir + "global";
        if (!Files.exists(Paths.get(cache))) {
            this.getFtpClient().downloadDirectory(remote, cache);
        }
        SystemUtils.copyDirectory(Paths.get(cache), target.toString());
    }

    @Override
    public void copyInstallablePlugin(InstallablePlugin plugin, Path target) {
        String cache = this.cacheDir + "plugins";
        Path path = Paths.get(cache);
        if (!Files.exists(path)) {
            String remote = this.getPluginsDir();
            if (!this.getFtpClient().existsDirectory(remote)) {
                this.getFtpClient().createDirectories(remote);
                return;
            }
        }

        SystemUtils.createParent(target);
        try {
            Files.copy(path, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearCache() {
        SystemUtils.deleteDirectory(Paths.get(this.cacheDir));
    }

    public void updateInCache(String group, String template) {
        String path = group + "/" + template;
        Path cache = Paths.get(this.cacheDir + path);
        SystemUtils.deleteDirectory(cache);
        this.addon.getFtpClient().downloadDirectory(this.getTemplateDir() + path, cache.toString());
    }

    private Path updateCache0(String path) {
        Path cache = Paths.get(this.cacheDir + path);
        if (!Files.exists(cache)) {
            this.addon.getFtpClient().downloadDirectory(this.getTemplateDir() + path, cache.toString());
        }
        return cache;
    }

    private void sendTemplateUpdate(String group, String template) {
        FtpTemplatesAddon.getInstance().getNode().getNetworkManager().sendPacketToNodes(new PacketFtpOutTemplateUpdated(group, template));
    }
}
