package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import jline.console.completer.Completer;
import lombok.*;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.TabCompletable;
import net.peepocloud.node.logging.ColoredLogger;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CommandCompleter implements Completer {

    private CommandManagerImpl commandManager;
    private ColoredLogger logger;

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {

            Collection<String> a;
            if (this.logger.getRunningSetup() != null && this.logger.getRunningSetup().getCurrentAvailable() != null) {
                a = this.logger.getRunningSetup().getCurrentAvailable();
            } else {
                a = this.commandManager.getCommands().keySet();
            }
            String s = buffer.toLowerCase();
            for (String command : a) {
                if (command.toLowerCase().startsWith(s)) {
                    candidates.add(command);
                }
            }


        } else {

            String testString = null;
            Collection<String> suggestions = null;
            if (this.logger.getRunningSetup() != null && this.logger.getRunningSetup().getCurrentAvailable() != null) {
                suggestions = this.logger.getRunningSetup().getCurrentAvailable();
                testString = buffer;
            } else {
                Command command = commandManager.getCommandByLine(buffer);
                if (command instanceof TabCompletable) {
                    String[] args = parseArgs(buffer);

                    suggestions = ((TabCompletable) command).tabComplete(commandManager.getConsole(), buffer, args);
                    testString = args[args.length - 1];
                }
            }

            if (suggestions != null) {
                for (String suggestion : suggestions)
                    if (testString == null || testString.isEmpty() || suggestion.toLowerCase().startsWith(testString.toLowerCase()))
                        candidates.add(suggestion);
            }

        }

        int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }

    private String[] parseArgs(String buffer) {
        String[] args = buffer.split(" ");
        if (args.length != 0)
            args = Arrays.copyOfRange(args, 1, args.length);

        if (buffer.endsWith(" ")) {
            args = args.length == 0 ? new String[]{""} : Arrays.copyOf(args, args.length + 1);
            if (args.length != 0)
                args[args.length - 1] = "";
        }
        return args;
    }

}
