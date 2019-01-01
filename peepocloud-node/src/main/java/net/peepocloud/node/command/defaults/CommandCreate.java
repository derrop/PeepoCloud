package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import net.peepocloud.lib.users.User;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.server.GroupMode;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.command.TabCompletable;
import net.peepocloud.node.api.server.TemplateStorage;
import net.peepocloud.node.api.setup.Setup;
import net.peepocloud.node.api.setup.type.*;

import java.util.*;
import java.util.stream.Collectors;

public class CommandCreate extends Command implements TabCompletable {
    public CommandCreate() {
        super("create");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 2 && args.length != 3 && args.length != 5) {
            sendHelp(sender);
            return;
        }

        String name = args[1];

        switch (args[0].toLowerCase()) {
            case "bungeegroup": {
                if (args.length != 2) {
                    sendHelp(sender);
                    return;
                }

                if (PeepoCloudNode.getInstance().getBungeeGroups().containsKey(name)) {
                    sender.createLanguageMessage("command.create.bungeegroup.alreadyExists").replace("%name%", name).send();
                    return;
                }
                Setup.startSetupAsync(new YamlConfigurable(), PeepoCloudNode.getInstance().getLogger(), setup -> {

                    setup.request(
                            "groupMode",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.groupmode.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.groupmode.invalid"),
                            new EnumSetupAcceptable(GroupMode.class),
                            Arrays.stream(GroupMode.values()).map(GroupMode::name).collect(Collectors.toList())
                    );

                    GroupMode groupMode = GroupMode.valueOf(setup.getData().getString("groupMode").toUpperCase());

                    Object[] storages = PeepoCloudNode.getInstance().getTemplateStorages().stream().map(TemplateStorage::getName).toArray();

                    String storage = null;

                    if (storages.length <= 1) {
                        storage = storages.length == 1 ? String.valueOf(storages[0]) : "local";
                    } else {
                        setup.request(
                                "templateStorage",
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.templatestorage.provide").replace("%storages%", Arrays.toString(storages)),
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.templatestorage.invalid").replace("%storages%", Arrays.toString(storages)),
                                new ArraySetupAcceptable<>(storages),
                                Arrays.stream(storages).map(Object::toString).collect(Collectors.toList())
                        );
                        storage = setup.getData().getString("templateStorage");
                    }

                    List<Template> templates = new ArrayList<>(); //not Arrays.asList because we cannot use the add method with this (for the create template command)
                    templates.add(new Template("default", storage));

                    setup.request(
                            "memory",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.memory.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.memory.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            Arrays.asList("128", "256", "512", "1024", "2048", "4096", "8192", "16384", "32768")
                    );
                    int memory = setup.getData().getInt("memory");
                    memory = memory >= 64 ? memory : memory * 1024; //>= 64 = gb; < 64 = mb

                    setup.request(
                            "minServers",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.minServers.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.minServers.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            null
                    );
                    int minServers = setup.getData().getInt("minServers");

                    setup.request(
                            "maxServers",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.maxServers.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.maxServers.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            null
                    );
                    int maxServers = setup.getData().getInt("maxServers");

                    setup.request(
                            "startPort",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.startPort.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.startPort.invalid"),
                            (IntegerSetupAcceptable) input -> input <= 65535 && input > 0,
                            null
                    );
                    int startPort = setup.getData().getInt("startPort");

                    BungeeGroup group = new BungeeGroup(name, groupMode, templates, memory, minServers, maxServers, startPort, false);

                    PeepoCloudNode.getInstance().getGroupsConfig().createGroup(group, success -> {
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
                if (args.length != 2) {
                    sendHelp(sender);
                    return;
                }

                if (PeepoCloudNode.getInstance().getMinecraftGroups().containsKey(name)) {
                    sender.createLanguageMessage("command.create.minecraftgroup.alreadyExists").replace("%name%", name).send();
                    return;
                }
                Setup.startSetupAsync(new YamlConfigurable(), PeepoCloudNode.getInstance().getLogger(), setup -> {
                    setup.request(
                            "groupMode",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.groupmode.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.groupmode.invalid"),
                            new EnumSetupAcceptable(GroupMode.class),
                            Arrays.stream(GroupMode.values()).map(GroupMode::name).collect(Collectors.toList())
                    );

                    GroupMode groupMode = GroupMode.valueOf(setup.getData().getString("groupMode").toUpperCase());

                    Object[] storages = PeepoCloudNode.getInstance().getTemplateStorages().stream().map(TemplateStorage::getName).toArray();

                    String storage = null;

                    if (storages.length <= 1) {
                        storage = storages.length == 1 ? String.valueOf(storages[0]) : "local";
                    } else {
                        setup.request(
                                "templateStorage",
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.templatestorage.provide").replace("%storages%", Arrays.toString(storages)),
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.bungeegroup.templatestorage.invalid").replace("%storages%", Arrays.toString(storages)),
                                new ArraySetupAcceptable<>(storages),
                                Arrays.stream(storages).map(Object::toString).collect(Collectors.toList())
                        );
                        storage = setup.getData().getString("templateStorage");
                    }

                    List<Template> templates = new ArrayList<>(); //not Arrays.asList because we cannot use the add method with this (for the create template command)
                    templates.add(new Template("default", storage));

                    setup.request(
                            "memory",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.memory.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.memory.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            Arrays.asList("128", "256", "512", "1024", "2048", "4096", "8192", "16384", "32768")
                    );
                    int memory = setup.getData().getInt("memory");
                    memory = memory >= 64 ? memory : memory * 1024; //>= 64 = gb; < 64 = mb

                    setup.request(
                            "minServers",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.minServers.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.minServers.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            null
                    );
                    int minServers = setup.getData().getInt("minServers");

                    setup.request(
                            "maxServers",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.maxServers.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.maxServers.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            null
                    );
                    int maxServers = setup.getData().getInt("maxServers");

                    setup.request(
                            "startPort",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.startPort.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.startPort.invalid"),
                            (IntegerSetupAcceptable) input -> input > 0 && input <= 65535,
                            null
                    );
                    int startPort = setup.getData().getInt("startPort");

                    setup.request(
                            "maxPlayers",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.maxPlayers.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.maxPlayers.invalid"),
                            (IntegerSetupAcceptable) input -> true,
                            null
                    );
                    int maxPlayers = setup.getData().getInt("maxPlayers");

                    setup.request(
                            "motd",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.motd.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.motd.invalid"),
                            (StringSetupAcceptable) input -> true,
                            null
                    );
                    String motd = setup.getData().getString("motd");

                    setup.request(
                            "fallback",
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.fallback.provide"),
                            PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.fallback.invalid"),
                            (BooleanSetupAcceptable) input -> true,
                            Arrays.asList("true", "false")
                    );
                    boolean fallback = setup.getData().getBoolean("fallback");

                    if (fallback) {
                        setup.request(
                                "fallbackPermission",
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.fallbackPermission.provide"),
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("command.create.minecraftgroup.fallbackPermission.invalid"),
                                (StringSetupAcceptable) input -> true,
                                null
                        );
                    }
                    String fallbackPermission = setup.getData().getString("fallbackPermission");

                    MinecraftGroup group = new MinecraftGroup(name, groupMode, templates, memory, minServers, maxServers, maxPlayers, motd, startPort, false, "default", fallback, fallbackPermission);

                    PeepoCloudNode.getInstance().getGroupsConfig().createGroup(group, success -> {
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
                if (args.length != 5) {
                    sendHelp(sender);
                    return;
                }

                String templateName = args[3];
                String templateStorage = args[4];

                if (args[1].equalsIgnoreCase("minecraftgroup")) {
                    MinecraftGroup group = PeepoCloudNode.getInstance().getMinecraftGroup(args[2]);
                    if (group == null) {
                        sender.sendMessageLanguageKey("command.create.template.minecraftgroup.groupNotFound");
                        return;
                    }
                    if (group.getTemplates().stream().anyMatch(template -> template.getName().equalsIgnoreCase(templateName))) {
                        sender.sendMessageLanguageKey("command.create.template.minecraftgroup.alreadyExists");
                        return;
                    }
                    group.getTemplates().add(new Template(templateName, templateStorage));
                    group.update();
                    sender.sendMessageLanguageKey("command.create.template.minecraftgroup.success");

                } else if (args[1].equalsIgnoreCase("bungeegroup")) {
                    BungeeGroup group = PeepoCloudNode.getInstance().getBungeeGroup(args[2]);
                    if (group == null) {
                        sender.sendMessageLanguageKey("command.create.template.bungeegroup.groupNotFound");
                        return;
                    }
                    if (group.getTemplates().stream().anyMatch(template -> template.getName().equalsIgnoreCase(templateName))) {
                        sender.sendMessageLanguageKey("command.create.template.bungeegroup.alreadyExists");
                        return;
                    }
                    group.getTemplates().add(new Template(templateName, templateStorage));
                    group.update();
                    sender.sendMessageLanguageKey("command.create.template.bungeegroup.success");

                } else {
                    sendHelp(sender);
                }
            }
            break;

            case "user": {
                if (args.length != 3) {
                    sendHelp(sender);
                    return;
                }

                User user = new User(args[1], args[2], SystemUtils.randomString(32));
                PeepoCloudNode.getInstance().getUserManager().addUser(user);
                sender.sendMessage("&aUser successfully created, the API Token is: &e" + user.getApiToken());
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
                "create template <minecraftgroup|bungeegroup> <groupName> <name> <storage>",
                "create node <name>",
                "create user <name> <password>"
        );
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        return args.length == 1 ? Arrays.asList("bungeegroup", "minecraftgroup", "template", "node", "user") :
                args[0].equalsIgnoreCase("template") ?
                        args.length == 2 ?
                                Arrays.asList("minecraftgroup", "bungeegroup") :
                                args.length == 3 ?
                                        args[1].equalsIgnoreCase("minecraftgroup") ? PeepoCloudNode.getInstance().getMinecraftGroups().keySet() :
                                                args[1].equalsIgnoreCase("bungeegroup") ? PeepoCloudNode.getInstance().getBungeeGroups().keySet() :
                                                        Collections.emptyList() :
                                        args.length == 5 ? PeepoCloudNode.getInstance().getTemplateStorages().stream().map(TemplateStorage::getName).collect(Collectors.toList()) :
                                                Collections.emptyList()
                        : Collections.emptyList()
                ;
    }

}
