package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.api.server.GroupMode;
import net.peepocloud.api.server.bungee.BungeeGroup;
import net.peepocloud.api.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.command.Command;
import net.peepocloud.node.command.CommandSender;
import net.peepocloud.node.command.TabCompletable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CommandConfig extends Command implements TabCompletable {
    public CommandConfig() {
        super("config", null, "conf");
    }

    private Collection<Field> bungeeGroupFields = Arrays.stream(BungeeGroup.class.getDeclaredFields())
            .filter(field -> !field.getName().equals("name") && field.getType().equals(String.class) || field.getType().equals(int.class) || field.getType().equals(GroupMode.class))
            .collect(Collectors.toList());
    private Collection<Field> minecraftGroupFields = Arrays.stream(MinecraftGroup.class.getDeclaredFields())
            .filter(field -> !field.getName().equals("name") && field.getType().equals(String.class) || field.getType().equals(int.class) || field.getType().equals(GroupMode.class))
            .collect(Collectors.toList());

    {
        this.minecraftGroupFields.forEach(field -> field.setAccessible(true));
        this.bungeeGroupFields.forEach(field -> field.setAccessible(true));
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 5) {
            sendUsage(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("edit")) {
            if (args[1].equalsIgnoreCase("bungeegroup")) {
                BungeeGroup group = PeepoCloudNode.getInstance().getBungeeGroup(args[2]);
                if (group == null) {
                    sender.createLanguageMessage("command.config.edit.bungeegroup.groupNotFound").replace("%name%", args[2]).send();
                    return;
                }

                Field field = this.findField(this.bungeeGroupFields, args[3]);

                if (field == null) {
                    sendUsage(sender);
                    return;
                }

                String val = args[4];

                if (this.edit(group, val, field, sender)) {
                    PeepoCloudNode.getInstance().updateBungeeGroup(group);
                    sender.createLanguageMessage("command.config.edit.bungeegroup.success").replace("%field%", field.getName()).replace("%val%", val).send();
                }
            } else if (args[1].equalsIgnoreCase("minecraftgroup")) {
                MinecraftGroup group = PeepoCloudNode.getInstance().getMinecraftGroup(args[2]);
                if (group == null) {
                    sender.createLanguageMessage("command.config.edit.minecraftgroup.groupNotFound").replace("%name%", args[2]).send();
                    return;
                }

                Field field = this.findField(this.minecraftGroupFields, args[3]);

                if (field == null) {
                    sendUsage(sender);
                    return;
                }

                String val = args[4];

                if (this.edit(group, val, field, sender)) {
                    PeepoCloudNode.getInstance().updateMinecraftGroup(group);
                    sender.createLanguageMessage("command.config.edit.minecraftgroup.success").replace("%field%", field.getName()).replace("%val%", val).send();
                }
            } else {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }

    private void sendUsage(CommandSender sender) {
        for (Field field : this.bungeeGroupFields)
            sender.sendMessage("config edit bungeeGroup <group> " + field.getName() + " value");
        for (Field field : this.minecraftGroupFields)
            sender.sendMessage("config edit minecraftGroup <group> " + field.getName() + " value");
    }

    private Field findField(Collection<Field> fields, String name) {
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field;
            }
        }
        return null;
    }

    private boolean edit(Object group, String val, Field field, CommandSender sender) {
        String a = group instanceof BungeeGroup ? "bungeegroup" : group instanceof MinecraftGroup ? "minecraftgroup" : null;
        try {
            if (field.getType().equals(String.class)) {
                field.set(group, val);
                return true;
            } else if (field.getType().equals(int.class)) {
                if (!SystemUtils.isInteger(val)) {
                    sender.createLanguageMessage("command.config.edit." + a + ".noInt").replace("%val%", val).send();
                    return false;
                }
                field.set(group, Integer.parseInt(val));
                return true;
            } else if (field.getType().equals(GroupMode.class)) {
                GroupMode groupMode;
                try {
                    groupMode = GroupMode.valueOf(val.toUpperCase());
                } catch (Exception e) {
                    sender.createLanguageMessage("command.config.edit." + a + ".groupModeInvalid").replace("%val%", val).send();
                    return false;
                }
                field.set(group, groupMode);
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        if (args.length == 1)
            return Collections.singletonList("edit");
        if (args.length == 2)
            return Arrays.asList("minecraftGroup", "bungeeGroup");
        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("minecraftgroup")) {
                return PeepoCloudNode.getInstance().getMinecraftGroups().keySet();
            } else if (args[1].equalsIgnoreCase("bungeegroup")) {
                return PeepoCloudNode.getInstance().getBungeeGroups().keySet();
            }
        }
        if (args.length == 4) {
            if (args[1].equalsIgnoreCase("minecraftgroup")) {
                return this.minecraftGroupFields.stream().map(Field::getName).collect(Collectors.toList());
            } else if (args[1].equalsIgnoreCase("bungeegroup")) {
                return this.bungeeGroupFields.stream().map(Field::getName).collect(Collectors.toList());
            }
        }
        return null;
    }
}
