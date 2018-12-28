package net.peepocloud.addons.serverselector;

import net.peepocloud.addons.serverselector.listener.ServerStartListener;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.serverselector.signselector.AnimatedSignLayout;
import net.peepocloud.lib.serverselector.signselector.SignLayout;
import net.peepocloud.lib.serverselector.signselector.SignSelectorConfig;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.node.api.addon.node.NodeAddon;
import net.peepocloud.node.api.database.Database;

import java.util.concurrent.ExecutionException;

public class ServerSelectorAddon extends NodeAddon {

    private SignSelectorConfig signSelectorConfig = new SignSelectorConfig(20, "STAINED_GLASS", (byte) 1, (byte) 5, (byte) 13, (byte) 3, (byte) 14);

    private AnimatedSignLayout loadingLayout = new AnimatedSignLayout(new SignLayout[]{
            new SignLayout("loading", new String[]{"%groupName%", "- NO SERVER AVAILABLE - ", "", "%groupName%"})}, 1);
    private AnimatedSignLayout maintenanceLayout = new AnimatedSignLayout(new SignLayout[]{
            new SignLayout("maintenance", new String[]{"%groupName%", "- MAINTENANCE - ", "", "%groupName%"})}, 1);

    private SimpleJsonObject signSelectorContainer = new SimpleJsonObject()
            .append("signLayouts", new SignLayout[]{new SignLayout("default",
                    new String[]{"%groupName% - %serverName%", "%motd%", "%onlinePlayers%/%maxPlayers%", "%serverState%"})})
            .append("maintenanceLayout", this.maintenanceLayout)
            .append("loadingLayout", this.loadingLayout)
            .append("serverSigns", new ServerSign[0]);

    @Override
    public void onLoad() {
        Database database = super.getNode().getDatabaseManager().getDatabase("serverSelector");
        Database configDatabase = super.getNode().getDatabaseManager().getDatabase("internal_configs");


        configDatabase.contains("signSelector").thenAccept(contains -> {
            if(!contains)
                configDatabase.insert("signSelector", new SimpleJsonObject().append("signSelectorConfig", this.signSelectorConfig));
            else {
                try {
                    this.signSelectorConfig = configDatabase.get("signSelector").get().getObject("signSelectorConfig", SignSelectorConfig.class);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        database.contains("signSelector").thenAccept(contains -> {
            if(!contains)
                database.insert("signSelector", this.signSelectorContainer);
            else {
                try {
                    this.signSelectorContainer = database.get("signSelector").get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        super.getNode().getEventManager().registerListener(this, new ServerStartListener(this));
    }

    public SignSelectorConfig getSignSelectorConfig() {
        return signSelectorConfig;
    }

    public SimpleJsonObject getSignSelectorContainer() {
        return signSelectorContainer;
    }


}
