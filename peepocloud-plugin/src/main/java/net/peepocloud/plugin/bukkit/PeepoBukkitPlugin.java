package net.peepocloud.plugin.bukkit;

import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.api.bukkit.PeepoCloudBukkitAPI;
import net.peepocloud.plugin.bukkit.command.CloudPluginCommand;
import net.peepocloud.plugin.bukkit.command.subcommand.signselector.CreateSignSubCommand;
import net.peepocloud.plugin.bukkit.command.subcommand.signselector.RemoveSignSubCommand;
import net.peepocloud.plugin.bukkit.command.subcommand.signselector.SaveSignsSubCommand;
import net.peepocloud.plugin.bukkit.listener.SignListener;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignSelector;
import net.peepocloud.plugin.network.packet.in.PacketInAPISignSelector;
import net.peepocloud.plugin.network.packet.in.PacketInUpdateServerInfo;
import org.bukkit.Bukkit;
import java.nio.file.Paths;

public class PeepoBukkitPlugin extends PeepoCloudPlugin implements PeepoCloudBukkitAPI {
    private BukkitLauncher plugin;

    private MinecraftServerInfo currentServerInfo;
    private CloudPluginCommand cloudPluginCommand = new CloudPluginCommand();
    private SignSelector signSelector;

    PeepoBukkitPlugin(BukkitLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;

        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        this.plugin.getCommand("cloudplugin").setExecutor(this.cloudPluginCommand);
    }

    @Override
    public void bootstrap() {
        super.registerNetworkHandler(new BukkitNetworkHandler(this));

        super.getPacketManager().registerPacket(new PacketInUpdateServerInfo());
        super.getPacketManager().registerPacket(new PacketInAPISignSelector());

        super.bootstrap();
    }

    @Override
    public void handleSuccessfulLogin() {

    }

    @Override
    public boolean isBungee() {
        return false;
    }

    @Override
    public boolean isBukkit() {
        return true;
    }

    public void setupSignSelector(SignSelector signSelector) {
        if(this.signSelector == null) {
            this.signSelector = signSelector;
            this.registerNetworkHandler(signSelector);

            SignListener signListener = new SignListener(this, signSelector);
            signSelector.setTitleRunnable(signListener.getTitleScheduler());
            Bukkit.getPluginManager().registerEvents(signListener, this.plugin);

            this.cloudPluginCommand.registerSubCommand(new CreateSignSubCommand(signSelector.getSignProvider()));
            this.cloudPluginCommand.registerSubCommand(new RemoveSignSubCommand(signSelector.getSignProvider()));
            this.cloudPluginCommand.registerSubCommand(new SaveSignsSubCommand(signSelector.getSignProvider()));

            if(signSelector.getChildren().size() > 0)
                signSelector.start(super.scheduler);
        }
    }

    public void updateCurrentServerInfo(MinecraftServerInfo serverInfo) {
        if(this.currentServerInfo == null)
            this.currentServerInfo = serverInfo;
        else
            this.currentServerInfo.updateFrom(serverInfo);
    }

    public MinecraftServerInfo getCurrentServerInfo() {
        return currentServerInfo;
    }

    public CloudPluginCommand getCloudPluginCommand() {
        return cloudPluginCommand;
    }

    public SignSelector getSignSelector() {
        return signSelector;
    }

    @Override
    public BukkitLauncher getPlugin() {
        return plugin;
    }
}
