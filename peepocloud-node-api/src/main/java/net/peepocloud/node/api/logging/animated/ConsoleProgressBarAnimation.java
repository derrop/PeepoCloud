package net.peepocloud.node.api.logging.animated;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.node.api.logging.AbstractConsoleAnimation;
import net.peepocloud.node.api.logging.ConsoleLogger;

/**
 * Represents a progess bar animation in the console that is updated all 10 milliseconds
 */
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

    /**
     * Creates a new {@link ConsoleProgressBarAnimation}
     * @param logger the logger in which the animation will be displayed
     * @param fullLength the maximum of the animation
     * @param startValue the initial value for this animation
     * @param progressChar the {@link Character} for each percent in the animation
     * @param lastProgressChar the {@link Character} which is at the last position of the animation
     * @param prefix the prefix for this animation
     * @param suffix the suffix for this animation
     */
    public ConsoleProgressBarAnimation(ConsoleLogger logger, int fullLength, int startValue, char progressChar, char lastProgressChar, String prefix, String suffix) {
        super(logger);
        this.length = fullLength;
        this.currentValue = startValue;
        this.progressChar = progressChar;
        this.lastProgressChar = lastProgressChar;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Deprecated
    @Override
    public void start(ConsoleLogger logger) {
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

    protected String formatCurrentValue(long currentValue) {
        return String.valueOf(currentValue);
    }

    protected String formatLength(long length) {
        return String.valueOf(length);
    }

    protected String formatTime(long millis) {
        long seconds = (millis / 1000);
        String min = String.valueOf(seconds / 60);
        String sec = String.valueOf(seconds - ((seconds / 60) * 60));
        if (min.length() == 1)
            min = "0" + min;
        if (sec.length() == 1)
            sec = "0" + sec;
        return min + ":" + sec;
    }

    private void doUpdate(double percent) {
        char[] chars = new char[100];
        for (int i = 0; i < (int) percent; i++) {
            chars[i] = progressChar;
        }
        for (int i = (int) percent; i < 100; i++) {
            chars[i] = ' ';
        }
        if ((int) percent > 0) {
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
        long millis = System.currentTimeMillis() - start;
        long time = millis / 1000;
        return input == null ? "" : input
                .replace("%value%", formatCurrentValue(this.currentValue))
                .replace("%length%", formatLength(this.length))
                .replace("%percent%", String.format("%.2f", percent))
                .replace("%time%", formatTime(millis))
                .replace("%bps%", String.valueOf(time == 0 ? "0" : ((currentValue / 1000) / time) * 8)); //bits per second
    }
}
