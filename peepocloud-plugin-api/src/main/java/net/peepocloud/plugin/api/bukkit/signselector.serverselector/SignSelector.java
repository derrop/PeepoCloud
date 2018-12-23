package net.peepocloud.plugin.api.bukkit.signselector.serverselector;


import net.peepocloud.lib.signlayout.AnimatedSignLayout;
import net.peepocloud.lib.signlayout.SignLayout;
import net.peepocloud.lib.signlayout.sign.ServerSign;

import java.util.HashMap;
import java.util.Map;

public class SignSelector {
    private Map<String, ServerSign> serverSigns = new HashMap<>();
    private Map<String, SignLayout> signLayouts = new HashMap<>();
    private AnimatedSignLayout loadingLayout;
    private AnimatedSignLayout maintenaceLayout;

    public SignSelector(ServerSign[] serverSigns, SignLayout[] signLayouts, AnimatedSignLayout loadingLayout, AnimatedSignLayout maintenanceLayout) {
        for(ServerSign serverSign : serverSigns)
            this.serverSigns.put(serverSign.getGroupName(), serverSign);
        for(SignLayout signLayout : signLayouts)
            this.signLayouts.put(signLayout.getLayoutName(), signLayout);

        this.loadingLayout = loadingLayout;
        this.maintenaceLayout = maintenanceLayout;
    }

    public Map<String, ServerSign> getServerSigns() {
        return serverSigns;
    }

    public Map<String, SignLayout> getSignLayouts() {
        return signLayouts;
    }

    public AnimatedSignLayout getLoadingLayout() {
        return loadingLayout;
    }

    public AnimatedSignLayout getMaintenaceLayout() {
        return maintenaceLayout;
    }
}
