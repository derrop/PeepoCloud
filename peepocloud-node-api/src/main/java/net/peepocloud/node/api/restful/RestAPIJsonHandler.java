package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import com.google.gson.JsonParseException;
import net.peepocloud.lib.config.json.SimpleJsonObject;

import java.nio.charset.StandardCharsets;

public interface RestAPIJsonHandler extends RestAPIHandler {
    @Override
    default void handle(RestAPIClient client) {
        try {
            String s = client.getRequestBody().asString();
            if (s == null) {
                client.sendResponse(400, "{\"success\":false,\"reason\":\"Invalid json request\"}".getBytes(StandardCharsets.UTF_8));
                return;
            }
            SimpleJsonObject jsonObject = new SimpleJsonObject(s);
            this.handleJson(client, jsonObject);
        } catch (JsonParseException e) {
            client.sendResponse(400, "{\"success\":false,\"reason\":\"Invalid json request\"}".getBytes(StandardCharsets.UTF_8));
        }
    }

    void handleJson(RestAPIClient client, SimpleJsonObject jsonObject);
}
