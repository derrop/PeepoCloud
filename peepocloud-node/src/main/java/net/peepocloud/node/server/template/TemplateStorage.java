package net.peepocloud.node.server.template;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.node.server.process.CloudProcess;

import java.nio.file.Path;

public abstract class TemplateStorage {

    public abstract String getName();

    public abstract void copy(String group, Template template, Path target);

    public abstract void deploy(CloudProcess process, Path path, Template template);

    public abstract void delete(String group, Template template);

    public abstract void create(String group, Template template);

}
