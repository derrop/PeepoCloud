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
import net.peepocloud.node.server.process.CloudProcess;

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
    public void copyToTemplate(CloudProcess process, Template template) {
        SystemUtils.copyDirectory(process.getDirectory(), "templates/" + process.getGroupName() + "/" + template.getName());
    }

    @Override
    public void copyFilesToTemplate(CloudProcess process, Template template, String[] files) {
        String dir = process.getDirectory().toString();
        String templateDir = "templates/" + process.getGroupName() + "/" + template.getName();
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
