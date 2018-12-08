package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.server.bungee.BungeeGroup;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CommandConfig extends Command {
    public CommandConfig() {
        super("config", null, "conf");
    }

    private Collection<Field> bungeeGroupFields = Arrays.stream(BungeeGroup.class.getDeclaredFields())
            .filter(field -> field.getType().equals(String.class) || field.getType().equals(int.class) || field.getType().equals(GroupMode.class))
            .collect(Collectors.toList());
    private Collection<Field> minecraftGroupFields = Arrays.stream(MinecraftGroup.class.getDeclaredFields())
            .filter(field -> field.getType().equals(String.class) || field.getType().equals(int.class) || field.getType().equals(GroupMode.class))
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
                BungeeGroup group = NeverCloudNode.getInstance().getBungeeGroup(args[2]);
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
                    NeverCloudNode.getInstance().updateBungeeGroup(group);
                    sender.createLanguageMessage("command.config.edit.bungeegroup.success").replace("%field%", field.getName()).replace("%val%", val).send();
                }
            } else if (args[1].equalsIgnoreCase("minecraftgroup")) {
                MinecraftGroup group = NeverCloudNode.getInstance().getMinecraftGroup(args[2]);
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
                    NeverCloudNode.getInstance().updateMinecraftGroup(group);
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
                    groupMode = GroupMode.valueOf(val);
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
}
