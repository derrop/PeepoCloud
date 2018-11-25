package net.nevercloud.node.logging.animated;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.nevercloud.node.logging.AbstractConsoleAnimation;
import net.nevercloud.node.logging.ColoredLogger;

import java.util.function.Consumer;

public class ConsoleProgressBarAnimation extends AbstractConsoleAnimation {
    @Getter
    @Setter
    private int length;
    @Getter
    @Setter
    private int currentValue;
    private char progressChar;
    private String prefix;
    private String suffix;
    private char lastProgressChar;
    private long start;

    public ConsoleProgressBarAnimation(ColoredLogger logger, int fullLength, int startValue, char progressChar, char lastProgressChar, String prefix, String suffix) {
        super(logger);
        this.length = fullLength;
        this.currentValue = startValue;
        this.progressChar = progressChar;
        this.lastProgressChar = lastProgressChar;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public static Consumer<Integer> startWithConsumer(ColoredLogger logger, int fullLength, int startValue, char progressChar, char lastProgressChar, String prefix, String suffix) {
        if (logger.isAnimationRunning())
            return null;
        ConsoleProgressBarAnimation animation = new ConsoleProgressBarAnimation(logger, fullLength, startValue, progressChar, lastProgressChar, prefix, suffix);
        logger.startAnimation(animation);
        return integer -> animation.currentValue = integer;
    }

    @Deprecated
    @Override
    public void start(ColoredLogger logger) {
        this.start = System.currentTimeMillis();
        while (this.currentValue < this.length) {
            this.doUpdate(((double) this.currentValue / (double) this.length) * 100.0D);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.doUpdate(100.0D);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUpdate(double percent) {
        char[] chars = new char[100];
        for (int i = 0; i < (int) percent; i++) {
            chars[i] = progressChar;
        }
        for (int i = (int) percent; i < 100; i++) {
            chars[i] = ' ';
        }
        if ((int) percent > 0 && lastProgressChar != -1) {
            chars[(int) percent - 1] = lastProgressChar;
        } else {
            chars[0] = lastProgressChar;
        }
        print(
                format(prefix, percent),
                String.valueOf(chars),
                format(suffix, percent)
        );
    }

    private String format(String input, double percent) {
        long time = (System.currentTimeMillis() - start) / 1000;
        return input == null ? "" : input
                .replace("%value%", String.valueOf(this.currentValue))
                .replace("%length%", String.valueOf(this.length))
                .replace("%percent%", String.format("%.2f", percent))
                .replace("%time%", String.valueOf(time))
                .replace("%bps%", String.valueOf(time == 0 ? "0" : ((currentValue / 1000) / time) * 8)); //bits per second
    }
}
