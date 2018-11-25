package net.nevercloud.node.logging;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.*;

public class ColoredLogger extends Logger {

    private ConsoleReader consoleReader;

    private final String prompt = ConsoleColor.RED + System.getProperty("user.name") + ConsoleColor.RESET + "@" + ConsoleColor.WHITE + "NeverCloudNode > " + ConsoleColor.YELLOW;
    @Getter
    private AbstractConsoleAnimation runningAnimation;
    private Consumer<String> lineAcceptor;

    public ColoredLogger(ConsoleReader consoleReader) throws IOException {
        super("NeverCloud Logger", null);
        this.consoleReader = consoleReader;

        AnsiConsole.systemInstall();

        if (!Files.exists(Paths.get("logs"))) {
            Files.createDirectory(Paths.get("logs"));
        }

        FileHandler fileHandler = new FileHandler("logs/nevercloud.log", 7 * 10000000, 8, true);
        fileHandler.setFormatter(new LogFileFormatter());
        addHandler(fileHandler);

        ColoredWriter colouredWriter = new ColoredWriter();
        colouredWriter.setLevel(Level.INFO);
        colouredWriter.setFormatter(new LogFormatter());
        addHandler(colouredWriter);

        System.setOut(new PrintStream(new LoggingOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(Level.SEVERE), true));
    }

    public String readLine() {
        String line = this.readLine0();
        if (this.lineAcceptor != null) {
            this.lineAcceptor.accept(line);
            return "";
        }
        return line;
    }

    private String readLine0() {
        this.updateAnimation();
        String line = null;
        try {
            line = this.consoleReader.readLine(this.prompt);
            this.consoleReader.setPrompt(ConsoleColor.RESET.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private String readLine1() {
        AtomicReference<String> line = new AtomicReference<>();
        this.lineAcceptor = line::set;
        while (line.get() == null) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.lineAcceptor = null;
        return line.get();
    }

    public String readLineUntil(Function<String, Boolean> function, String invalidInputMessage, String nullOn) {
        String line;
        while (!function.apply(line = this.readLine1()) || (line.equalsIgnoreCase(nullOn))) {
            if (line.equalsIgnoreCase(nullOn)) {
                return null;
            }
            System.out.println(invalidInputMessage);
        }
        return line;
    }

    public String readLineUntil(Function<String, Boolean> function, String invalidInputMessage) {
        return this.readLineUntil(function, invalidInputMessage, null);
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public void clearScreen() {
        try {
            this.consoleReader.clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.runningAnimation != null)
            this.runningAnimation.cursorUp = 2;
    }

    public void print(String line) {
        line = ConsoleColor.toColouredString(line);

        try {
            consoleReader.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE + line + Ansi.ansi().reset().toString());
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.updateAnimation();
    }

    public void printRaw(String line) {
        line = ConsoleColor.toColouredString(line);

        try {
            consoleReader.print(line + Ansi.ansi().reset().toString());
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.updateAnimation();
    }

    void print0(String line) {
        line = ConsoleColor.toColouredString(line);

        try {
            consoleReader.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE + line + Ansi.ansi().reset().toString());
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAnimationRunning() {
        return runningAnimation != null;
    }

    public void startAnimation(AbstractConsoleAnimation animation) {
        Preconditions.checkArgument(this.runningAnimation == null, "there is already another animation running in this logger");
        this.runningAnimation = animation;
        Thread thread = new Thread(() -> {
            animation.start(this);
            this.runningAnimation = null;
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void updateAnimation() {
        if (this.runningAnimation != null)
            this.runningAnimation.cursorUp++;
    }

    private class LogFileFormatter extends Formatter {
        private final DateFormat format = new SimpleDateFormat("dd:MM:yyyy kk:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(this.format.format(new Date()));
            builder.append('/');
            builder.append(record.getLevel().getLocalizedName());
            builder.append("] ");
            builder.append(ConsoleColor.stripColor(formatMessage(record)));
            builder.append('\n');
            if (record.getThrown() != null) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter writer = new PrintWriter(stringWriter);
                record.getThrown().printStackTrace(writer);
                builder.append(stringWriter);
            }
            return builder.toString();
        }
    }

    private class LogFormatter extends Formatter {
        private final DateFormat format = new SimpleDateFormat("dd:MM:yyyy kk:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(ConsoleColor.GRAY.toString());
            builder.append('[');
            builder.append(ConsoleColor.GREEN.toString());
            builder.append(this.format.format(new Date()));
            builder.append(ConsoleColor.GRAY.toString());
            builder.append('/');
            builder.append(ConsoleColor.YELLOW.toString());
            builder.append(record.getLevel().getLocalizedName());
            builder.append(ConsoleColor.GRAY.toString());
            builder.append("] ");
            builder.append(ConsoleColor.RESET.toString());
            builder.append(formatMessage(record));
            builder.append('\n');
            if (record.getThrown() != null) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter writer = new PrintWriter(stringWriter);
                record.getThrown().printStackTrace(writer);
                builder.append(stringWriter);
            }
            return builder.toString();
        }
    }

    private class ColoredWriter extends Handler {
        @Override
        public void publish(LogRecord record) {
            if (isLoggable(record)) {
                print(getFormatter().format(record));
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }

    @RequiredArgsConstructor
    private class LoggingOutputStream extends ByteArrayOutputStream {

        private String separator = System.getProperty("line.separator");
        private final Level level;

        @Override
        public void flush() throws IOException {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if (!contents.isEmpty() && !contents.equals(this.separator)) {
                logp(this.level, "", "", contents);
            }
        }
    }
}
