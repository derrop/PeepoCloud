package net.peepocloud.addons.templates.ftp;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import lombok.Getter;
import net.peepocloud.addons.templates.ftp.command.CommandFtpClearCache;
import net.peepocloud.addons.templates.ftp.network.packet.in.PacketFtpInTemplateUpdated;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.addon.node.NodeAddon;

import java.nio.file.Paths;

@Getter
public class FtpTemplatesAddon extends NodeAddon {

    @Getter
    private static FtpTemplatesAddon instance;

    private FtpClient ftpClient;
    private FtpTemplateStorage templateStorage;
    private String templateDir;

    @Override
    public void onLoad() {
        instance = this;

        if (!this.loadFtp()) {
            if (this.ftpClient != null) {
                this.ftpClient.close();
            }
            getNode().getExecutorService().execute(() -> {
                SystemUtils.sleepUninterruptedly(10000);
                while (this.isEnabled() && !this.loadFtp()) {
                    this.ftpClient = null;
                    System.out.println("&cCould not connect, trying to reconnect in 30 seconds");
                    SystemUtils.sleepUninterruptedly(30000);
                }
            });
        }

        this.templateStorage = new FtpTemplateStorage(this);

        if (getNode().registerTemplateStorage(this.templateStorage)) {
            System.out.println("&aSuccessfully registered the FTP TemplateStorage");
        } else {
            System.out.println("&cCould not register FTP TemplateStorage, there is already one with that name registered");
        }

        this.getNode().getCommandManager().registerCommand(this, new CommandFtpClearCache());
    }

    private boolean loadFtp() {
        YamlConfigurable config = loadConfig();

        String host = config.getString("sftp.host");
        int port = config.getInt("sftp.port");
        String username = config.getString("sftp.username");
        String password = config.getString("sftp.password");
        this.templateDir = config.getString("sftp.remoteTemplatesDirectory");
        if (this.templateDir != null && !this.templateDir.endsWith("/"))
            this.templateDir += "/";

        if (host == null || port == -1 || username == null || password == null || this.templateDir == null) {
            config.append("sftp.host", "host")
                    .append("sftp.port", 22)
                    .append("sftp.username", "")
                    .append("sftp.password", "")
                    .append("sftp.remoteTemplatesDirectory", "/home/peepocloud/templates/");
            createConfig(config);
            System.out.println("&cYour FTP-ConfigFile was created, please fill it with your data (\"" + getConfigFile().toString() + "\")");
            return false;
        }

        this.ftpClient = new FtpClient();

        return this.ftpClient.connect(host, username, password, port);
    }

    @Override
    public void onDisable() {
        this.ftpClient.close();
        instance = null;
        SystemUtils.deleteDirectory(Paths.get(this.templateStorage.getCacheDir()));
    }

    @Override
    public void initPacketHandlers(PacketManager packetManager) {
        packetManager.registerPacket(new PacketFtpInTemplateUpdated());
    }
}
