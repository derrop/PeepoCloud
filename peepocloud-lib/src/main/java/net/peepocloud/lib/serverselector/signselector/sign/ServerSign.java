package net.peepocloud.lib.serverselector.signselector.sign;

import net.peepocloud.lib.serverselector.Position;
import net.peepocloud.lib.serverselector.ServerSelectorChild;
import net.peepocloud.lib.serverselector.signselector.SignLayout;

public class ServerSign extends ServerSelectorChild {
    private Position position;
    private transient SignLayout basicLayout;

    public ServerSign(Position position, String groupName) {
        super(groupName);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setBasicLayout(SignLayout basicLayout) {
        this.basicLayout = basicLayout;
    }

    public SignLayout getBasicLayout() {
        return basicLayout;
    }
}
