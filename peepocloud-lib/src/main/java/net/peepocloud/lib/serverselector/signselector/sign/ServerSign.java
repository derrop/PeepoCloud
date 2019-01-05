package net.peepocloud.lib.serverselector.signselector.sign;

import net.peepocloud.lib.serverselector.ServerSelectorChild;
import net.peepocloud.lib.serverselector.signselector.SignLayout;

public class ServerSign extends ServerSelectorChild {
    private SignPosition position;
    private transient SignLayout basicLayout;

    public ServerSign(SignPosition position, String groupName, String savedOnGroup) {
        super(groupName, savedOnGroup);
        this.position = position;
    }

    public SignPosition getPosition() {
        return position;
    }

    public void setBasicLayout(SignLayout basicLayout) {
        this.basicLayout = basicLayout;
    }

    public SignLayout getBasicLayout() {
        return basicLayout;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj instanceof ServerSign) {
            ServerSign serverSign = (ServerSign) obj;
            return serverSign.position.equals(this.position) && serverSign.getGroupName().equalsIgnoreCase(super.getGroupName());
        }
        return false;
    }
}
