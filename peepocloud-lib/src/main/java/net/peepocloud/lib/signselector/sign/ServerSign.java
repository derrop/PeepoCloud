package net.peepocloud.lib.signselector.sign;


import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.signselector.SignLayout;

public class ServerSign {
    private SignLocation signLocation;
    private String groupName;
    private transient SignLayout basicLayout;
    private transient MinecraftServerInfo serverInfo;

    public ServerSign(SignLocation signLocation, String groupName) {
        this.signLocation = signLocation;
        this.groupName = groupName;
    }

    public SignLocation getSignLocation() {
        return signLocation;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setBasicLayout(SignLayout basicLayout) {
        this.basicLayout = basicLayout;
    }

    public SignLayout getBasicLayout() {
        return basicLayout;
    }

    public void setServerInfo(MinecraftServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public MinecraftServerInfo getServerInfo() {
        return serverInfo;
    }
}
