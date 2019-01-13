package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import lombok.*;
import net.peepocloud.node.api.addon.Addon;
import net.peepocloud.node.api.restful.handler.RestAPIHandler;

import java.util.Map;

@Data
@AllArgsConstructor
public class RestAPIHandlerInfo {
    private Addon addon;
    private RestAPIHandler handler;
    private Map<String, Long> rateLimits;

    public boolean isRateLimited(String host) {
        if (!this.rateLimits.containsKey(host))
            return false;
        long time = this.rateLimits.get(host);
        boolean a = time >= System.currentTimeMillis();
        if (!a) {
            this.rateLimits.remove(host);
        }
        return a;
    }
}
