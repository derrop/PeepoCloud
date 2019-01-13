package net.peepocloud.node.api.restful.handler;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import net.peepocloud.node.api.restful.RestAPIClient;
import net.peepocloud.node.api.restful.RestAPIRequestMethod;

import java.util.Collection;

public interface RestAPIHandler {

    String getPath();

    Collection<String> getRequiredHeaders();

    RestAPIRequestMethod[] supportedMethods();

    boolean usesRateLimit();

    default long overrideRateLimit() {
        return -1;
    }

    void handle(RestAPIClient client);

    /*void handleGet(RestAPIClient client);

    void handlePost(RestAPIClient client);

    void handlePut(RestAPIClient client);

    void handlePatch(RestAPIClient client);

    void handleDelete(RestAPIClient client);

    void handleCopy(RestAPIClient client);

    void handleHead(RestAPIClient client);

    void handleOptions(RestAPIClient client);

    void handleLink(RestAPIClient client);

    void handleUnlink(RestAPIClient client);

    void handlePurge(RestAPIClient client);

    void handleLock(RestAPIClient client);

    void handleUnlock(RestAPIClient client);

    void handlePropfind(RestAPIClient client);

    void handleView(RestAPIClient client);*/

}
