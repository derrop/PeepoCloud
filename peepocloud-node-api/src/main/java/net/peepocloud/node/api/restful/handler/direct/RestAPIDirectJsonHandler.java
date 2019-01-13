package net.peepocloud.node.api.restful.handler.direct;
/*
 * Created by Mc_Ruben on 08.01.2019
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.api.restful.RestAPIClient;
import net.peepocloud.node.api.restful.RestAPIRequestMethod;
import net.peepocloud.node.api.restful.handler.RestAPIJsonHandler;

import java.util.Collection;

@AllArgsConstructor
public abstract class RestAPIDirectJsonHandler implements RestAPIJsonHandler {

    public RestAPIDirectJsonHandler(String path, boolean usesRatelimit, Collection<String> requiredHeaders, RestAPIRequestMethod... supportedMethods) {
        this(path, usesRatelimit, -1, requiredHeaders, supportedMethods);
    }

    public RestAPIDirectJsonHandler(String path, boolean usesRatelimit, RestAPIRequestMethod... supportedMethods) {
        this(path, usesRatelimit, null, supportedMethods);
    }

    private String path;
    private boolean usesRatelimit;
    private long overrideRatelimit;
    private Collection<String> requiredHeaders;
    private RestAPIRequestMethod[] supportedMethods;

    @Override
    public final Collection<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    @Override
    public RestAPIRequestMethod[] supportedMethods() {
        return supportedMethods;
    }

    @Override
    public final String getPath() {
        return path;
    }

    @Override
    public final boolean usesRateLimit() {
        return usesRatelimit;
    }

    @Override
    public final long overrideRateLimit() {
        return overrideRatelimit;
    }

    public final void handleJson(RestAPIClient client, SimpleJsonObject jsonObject) {
        SimpleJsonObject response = this.handle(client, jsonObject);
        if (response != null) {
            client.sendResponse(200, response.toBytes());
        }
    }

    public abstract SimpleJsonObject handle(RestAPIClient client, SimpleJsonObject jsonObject);
}
