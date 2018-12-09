package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import jline.console.completer.Completer;
import lombok.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CommandCompleter implements Completer {

    private CommandManager commandManager;

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {

            String s = buffer.toLowerCase();
            for (String command : this.commandManager.getCommands().keySet()) {
                if (command.toLowerCase().startsWith(s)) {
                    candidates.add(command);
                }
            }

        } else {

            Command command = commandManager.getCommandByLine(buffer);
            if (command instanceof TabCompletable) {
                String[] args = buffer.split(" ");
                if (args.length != 0)
                    args = Arrays.copyOfRange(args, 1, args.length);

                if (buffer.endsWith(" ")) {
                    args = args.length == 0 ? new String[]{""} : Arrays.copyOf(args, args.length + 1);
                    if (args.length != 0)
                        args[args.length - 1] = "";
                }

                Collection<String> suggestions = ((TabCompletable) command).tabComplete(commandManager.getConsole(), buffer, args);
                if (suggestions != null) {
                    String testString = args[args.length - 1];
                    for (String suggestion : suggestions)
                        if (testString == null || testString.isEmpty() || suggestion.toLowerCase().startsWith(testString.toLowerCase()))
                            candidates.add(suggestion);
                }
            }

        }

        int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }

}
