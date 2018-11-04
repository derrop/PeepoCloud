package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import jline.console.ConsoleReader;
import lombok.*;
import net.nevercloud.node.commands.CommandManager;
import net.nevercloud.node.commands.defaults.CommandHelp;
import net.nevercloud.node.commands.defaults.CommandStop;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.logging.ConsoleColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
public class NeverCloudNode {

    @Getter
    private static NeverCloudNode instance;

    private ColoredLogger logger;
    private CommandManager commandManager;

    private boolean running = true;

    NeverCloudNode() throws IOException {
        Preconditions.checkArgument(instance == null, "instance is already defined");
        instance = this;

        this.createFiles();

        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        this.logger = new ColoredLogger(consoleReader);
        this.commandManager = new CommandManager(this.logger);


        this.initCommands(this.commandManager);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown0));
    }

    private void createFiles() throws IOException {
        if (!Files.exists(Paths.get("logs")))
            Files.createDirectory(Paths.get("logs"));
    }

    private void initCommands(CommandManager commandManager) {
        commandManager.registerCommands(
                new CommandHelp(),
                new CommandStop()
        );
    }

    public void shutdown() {
        shutdown0();
        System.exit(0);
    }

    private void shutdown0() {
        running = false;
        try {
            this.logger.getConsoleReader().print(ConsoleColor.RESET.toString());
            this.logger.getConsoleReader().drawLine();
            this.logger.getConsoleReader().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.logger.getConsoleReader().close();
    }

}
