package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import net.nevercloud.lib.config.yaml.YamlConfigurable;
import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.server.Template;
import net.nevercloud.lib.server.bungee.BungeeGroup;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.setup.Setup;
import net.nevercloud.node.setup.type.EnumSetupAcceptable;
import net.nevercloud.node.setup.type.IntegerSetupAcceptable;

import java.util.ArrayList;
import java.util.Collection;

public class CommandCreate extends Command {
    public CommandCreate() {
        super("create");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 2) {
            sendHelp(sender);
            return;
        }

        String name = args[1];

        switch (args[0].toLowerCase()) {
            case "bungeegroup": {
                Setup.startSetupAsync(new YamlConfigurable(), NeverCloudNode.getInstance().getLogger(), setup -> {
                    setup.request(
                            "groupMode",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.groupmode.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.groupmode.invalid"),
                            new EnumSetupAcceptable(GroupMode.class)
                    );

                    GroupMode groupMode = GroupMode.valueOf(setup.getData().getString("groupMode"));

                    Collection<Template> templates = new ArrayList<>(); //not Arrays.asList because we cannot use the add method with this (for the create template command)
                    templates.add(new Template("default"));

                    setup.request(
                            "memory",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.memory.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.memory.invalid"),
                            (IntegerSetupAcceptable) input -> true
                    );
                    int memory = setup.getData().getInt("memory");
                    memory = memory >= 64 ? memory * 1024 : memory; //>= 64 = gb; < 64 = mb

                    setup.request(
                            "minServers",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.minServers.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.minServers.invalid"),
                            (IntegerSetupAcceptable) input -> true
                    );
                    int minServers = setup.getData().getInt("minServers");

                    setup.request(
                            "maxServers",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.maxServers.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.maxServers.invalid"),
                            (IntegerSetupAcceptable) input -> true
                    );
                    int maxServers = setup.getData().getInt("maxServers");

                    BungeeGroup group = new BungeeGroup(name, groupMode, templates, memory, minServers, maxServers);

                    NeverCloudNode.getInstance().getGroupsConfig().createGroup(group, success -> {
                        if (success) {
                            sender.createLanguageMessage("command.create.bungeegroup.success").replace("%name%", group.getName()).send();
                        } else {
                            sender.createLanguageMessage("command.create.bungeegroup.alreadyExists").replace("%name%", group.getName()).send();
                        }
                    });

                });
            }
            break;

            case "minecraftgroup": {
                Setup.startSetupAsync(new YamlConfigurable(), NeverCloudNode.getInstance().getLogger(), setup -> {
                    setup.request(
                            "groupMode",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.groupmode.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.groupmode.invalid"),
                            new EnumSetupAcceptable(GroupMode.class)
                    );

                    GroupMode groupMode = GroupMode.valueOf(setup.getData().getString("groupMode"));

                    Collection<Template> templates = new ArrayList<>(); //not Arrays.asList because we cannot use the add method with this (for the create template command)
                    templates.add(new Template("default"));

                    setup.request(
                            "memory",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.memory.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.memory.invalid"),
                            (IntegerSetupAcceptable) input -> true
                    );
                    int memory = setup.getData().getInt("memory");
                    memory = memory >= 64 ? memory * 1024 : memory; //>= 64 = gb; < 64 = mb

                    setup.request(
                            "minServers",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.minServers.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.minServers.invalid"),
                            (IntegerSetupAcceptable) input -> true
                    );
                    int minServers = setup.getData().getInt("minServers");

                    setup.request(
                            "maxServers",
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.maxServers.provide"),
                            NeverCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.maxServers.invalid"),
                            (IntegerSetupAcceptable) input -> true
                    );
                    int maxServers = setup.getData().getInt("maxServers");

                    MinecraftGroup group = new MinecraftGroup(name, groupMode, templates, memory, minServers, maxServers);

                    NeverCloudNode.getInstance().getGroupsConfig().createGroup(group, success -> {
                        if (success) {
                            sender.createLanguageMessage("command.create.minecraftgroup.success").replace("%name%", group.getName()).send();
                        } else {
                            sender.createLanguageMessage("command.create.minecraftgroup.alreadyExists").replace("%name%", group.getName()).send();
                        }
                    });

                });
            }
            break;

            case "node": {

            }
            break;

            case "template": {

            }
            break;

            case "user": {

            }
            break;

            default:
                sendHelp(sender);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(
                "create bungeegroup <name>",
                "create minecraftgroup <name>",
                "create template <name>",
                "create node <name>",
                "create user <name>"
        );
    }
}
