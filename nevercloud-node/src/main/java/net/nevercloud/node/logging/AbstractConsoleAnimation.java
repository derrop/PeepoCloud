package net.nevercloud.node.logging;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.fusesource.jansi.Ansi;

@Getter
@AllArgsConstructor
public abstract class AbstractConsoleAnimation {

    private ColoredLogger logger;

    protected void print(String... input) {
        Ansi ansi = Ansi
                .ansi()
                .saveCursorPosition()
                .cursorUp(2)
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
