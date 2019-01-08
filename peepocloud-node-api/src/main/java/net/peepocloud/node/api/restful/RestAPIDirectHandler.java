package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 08.01.2019
 */

import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public abstract class RestAPIDirectHandler implements RestAPIHandler {

    public RestAPIDirectHandler(String path, boolean usesRatelimit, Collection<String> requiredHeaders, RestAPIRequestMethod... supportedMethods) {
        this(path, usesRatelimit, -1, requiredHeaders, supportedMethods);
    }

    public RestAPIDirectHandler(String path, boolean usesRatelimit, RestAPIRequestMethod... supportedMethods) {
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

}
