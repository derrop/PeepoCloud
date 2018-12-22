package net.peepocloud.node.api.logging;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.Ansi;

public abstract class AbstractConsoleAnimation {

    public AbstractConsoleAnimation(ConsoleLogger logger) {
        this.logger = logger;
    }

    @Getter
    private ConsoleLogger logger;
    public int cursorUp = 1;

    /**
     * Prints the specified input in one line to this logger and removes other lines printed by this method for the animation
     * @param input the strings to print
     */
    protected void print(String... input) {
        if (input.length == 0)
            return;
        input[0] = "&e" + input[0];
        Ansi ansi = Ansi
                .ansi()
                .saveCursorPosition()
                .cursorUp(this.cursorUp)
                .eraseLine(Ansi.Erase.ALL);
        for (String a : input) {
            ansi.a(a);
        }
        this.logger.printRaw(
                Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE +
                        ansi
                                .restoreCursorPosition()
                                .toString()
        );
    }

    @Deprecated
    public abstract void start(ConsoleLogger logger);

}
