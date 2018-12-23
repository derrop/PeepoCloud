package net.peepocloud.node.server.template;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.server.TemplateStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TemplateLocalStorage extends TemplateStorage {
    @Override
    public String getName() {
        return "local";
    }

    @Override
    public void copyToPath(String group, Template template, Path target) {
        Path dir = Paths.get("templates/" + group + "/" + template.getName());
        if (!Files.exists(dir)) {
            this.create(group, template);
        } else {
            SystemUtils.copyDirectory(dir, target.toString());
        }
    }

    @Override
    public void copyToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template) {
        this.copyToTemplate(directory, serverInfo.getGroupName(), template);
    }

    @Override
    public void copyFilesToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template, String[] files) {
        this.copyFilesToTemplate(directory, serverInfo.getGroupName(), template, files);
    }

    @Override
    public void copyToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template) {
        this.copyToTemplate(directory, proxyInfo.getGroupName(), template);
    }

    @Override
    public void copyFilesToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template, String[] files) {
        this.copyFilesToTemplate(directory, proxyInfo.getGroupName(), template, files);
    }

    private void copyToTemplate(Path directory, String group, Template template) {
        SystemUtils.copyDirectory(directory, "templates/" + group + "/" + template.getName());
    }

    private void copyFilesToTemplate(Path directory, String group, Template template, String[] files) {
        String dir = directory.toString();
        String templateDir = "templates/" + group + "/" + template.getName();
        for (String file : files) {
            Path path = Paths.get(dir, file);
            if (!Files.exists(path))
                continue;
            if (Files.isDirectory(path)) {
                SystemUtils.copyDirectory(path, templateDir + "/" + file);
            } else {
                try {
                    Files.copy(path, Paths.get(templateDir, file), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void deleteTemplate(MinecraftGroup group, Template template) {
        this.delete(group.getName(), template);
    }

    @Override
    public void deleteTemplate(BungeeGroup group, Template template) {
        this.delete(group.getName(), template);
    }

    private void delete(String group, Template template) {
        SystemUtils.deleteDirectory(Paths.get("templates/" + group + "/" + template.getName()));
    }

    @Override
    public void createTemplate(MinecraftGroup group, Template template) {
        this.create(group.getName(), template);
    }

    @Override
    public void createTemplate(BungeeGroup group, Template template) {
        this.create(group.getName(), template);
    }

    private void create(String group, Template template) {
        try {
            Files.createDirectories(Paths.get("templates/" + group + "/" + template.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
