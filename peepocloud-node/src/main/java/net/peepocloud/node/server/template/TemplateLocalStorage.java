package net.peepocloud.node.server.template;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.api.server.Template;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.server.process.CloudProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemplateLocalStorage extends TemplateStorage {
    @Override
    public String getName() {
        return "local";
    }

    @Override
    public void copy(String group, Template template, Path target) {
        Path dir = Paths.get("templates/" + group + "/" + template.getName());
        if (!Files.exists(dir)) {
            this.create(group, template);
        } else {
            SystemUtils.copyDirectory(dir, target.toString());
        }
    }

    @Override
    public void deploy(CloudProcess process, Path path, Template template) {
        SystemUtils.copyDirectory(process.getDirectory(), "templates/" + process.getGroupName() + "/" + template.getName());
    }

    @Override
    public void delete(String group, Template template) {
        SystemUtils.deleteDirectory(Paths.get("templates/" + group + "/" + template.getName()));
    }

    @Override
    public void create(String group, Template template) {
        try {
            Files.createDirectories(Paths.get("templates/" + group + "/" + template.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
