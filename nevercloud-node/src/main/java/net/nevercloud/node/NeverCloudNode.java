package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import jline.console.ConsoleReader;
import lombok.*;
import net.nevercloud.lib.json.SimpleJsonObject;
import net.nevercloud.node.addon.AddonManager;
import net.nevercloud.node.command.CommandManager;
import net.nevercloud.node.command.defaults.*;
import net.nevercloud.node.database.DatabaseManager;
import net.nevercloud.node.database.DatabaseLoader;
import net.nevercloud.node.addon.defaults.DefaultAddonManager;
import net.nevercloud.node.languagesystem.LanguagesManager;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.logging.ConsoleColor;
import net.nevercloud.node.addon.node.NodeAddon;
import net.nevercloud.node.updater.AutoUpdaterManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class NeverCloudNode {

    @Getter
    private static NeverCloudNode instance;

    private ColoredLogger logger;
    private CommandManager commandManager;

    private SimpleJsonObject internalConfig;

    private DatabaseManager databaseManager;
    private DatabaseLoader databaseLoader;

    private LanguagesManager languagesManager;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private AutoUpdaterManager autoUpdaterManager;

    private AddonManager<NodeAddon> nodeAddonManager;
    private DefaultAddonManager defaultAddonManager = new DefaultAddonManager();

    private boolean running = true;

    NeverCloudNode() throws IOException {
        Preconditions.checkArgument(instance == null, "instance is already defined");
        instance = this;

        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        this.logger = new ColoredLogger(consoleReader);

        this.internalConfig = SimpleJsonObject.load("internal/internalData.json");

        this.autoUpdaterManager = new AutoUpdaterManager();

        this.languagesManager = new LanguagesManager();

        this.databaseLoader = new DatabaseLoader("databaseAddons");
        this.databaseManager = this.databaseLoader.loadDatabaseManager(this);

        this.commandManager = new CommandManager(this.logger);

        this.initCommands(this.commandManager);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown0));

        this.nodeAddonManager = new AddonManager<>();
        this.reloadModules();
    }

    private void initCommands(CommandManager commandManager) {
        commandManager.registerCommands(
                new CommandHelp(),
                new CommandStop(),
                new CommandAddon(),
                new CommandLanguage(),
                new CommandUpdate(),
                new CommandVersion()
        );
    }

    private void shutdown0() {
        running = false;
        this.databaseLoader.shutdown();
        this.databaseManager.shutdown();

        this.nodeAddonManager.disableAndUnloadAddons();

        try {
            this.logger.getConsoleReader().print(ConsoleColor.RESET.toString());
            this.logger.getConsoleReader().drawLine();
            this.logger.getConsoleReader().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.logger.getConsoleReader().close();
    }

    public void shutdown() {
        shutdown0();
        System.exit(0);
    }

    public void reload() {
        this.reloadModules();
        this.reloadConfigs();
    }

    public void reloadConfigs() {

    }

    public void reloadModules() {
        this.nodeAddonManager.disableAndUnloadAddons();
        this.commandManager.getCommands().clear();
        this.initCommands(this.commandManager);
        try {
            this.nodeAddonManager.loadAddons("nodeAddons");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.nodeAddonManager.enableAddons();
    }

    public void saveInternalConfigFile() {
        this.internalConfig.saveAsFile("internal/internalData.json");
    }

    public String getMessage(String key) {
        return this.languagesManager.getMessage(key);
    }

    @Deprecated
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
