package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import java.util.Collection;

public interface TabCompletable {

    Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args);

}
