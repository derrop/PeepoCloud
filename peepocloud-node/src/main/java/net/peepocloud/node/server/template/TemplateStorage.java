package net.peepocloud.node.server.template;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.node.server.process.CloudProcess;

import java.nio.file.Path;

public abstract class TemplateStorage {

    public abstract String getName();

    public abstract void copyToPath(String group, Template template, Path target);

    public abstract void copyToTemplate(CloudProcess process, Template template);

    public abstract void copyFilesToTemplate(CloudProcess process, Template template, String[] files);

    public abstract void deleteTemplate(MinecraftGroup group, Template template);

    public abstract void deleteTemplate(BungeeGroup group, Template template);

    public abstract void createTemplate(MinecraftGroup group, Template template);

    public abstract void createTemplate(BungeeGroup group, Template template);

}
