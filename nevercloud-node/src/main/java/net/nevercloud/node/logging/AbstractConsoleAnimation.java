package net.nevercloud.node.logging;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import org.fusesource.jansi.Ansi;

public abstract class AbstractConsoleAnimation {

    public AbstractConsoleAnimation(ColoredLogger logger) {
        this.logger = logger;
    }

    @Getter
    private ColoredLogger logger;
    int cursorUp = 2;

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
        this.logger.print0(
                ansi
                        .restoreCursorPosition()
                        .toString()
        );
    }

    @Deprecated
    public abstract void start(ColoredLogger logger);

}
