package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

public interface RestAPIClient {

    URI getRequestURI();

    InetSocketAddress getRemoteAddress();

    Map<String, String> getParsedRequestQuery();

    RestAPIRequestMethod getRequestMethod();

    String getRequestHeader(String key);

    Map<String, String> getRequestHeaders();

    void setResponseHeader(String key, String value);

    RestAPIResponseBody getResponseBody();

    RestAPIRequestBody getRequestBody();

    void sendResponse(int code, InputStream inputStream);

    void sendResponse(int code, byte[] bytes);

    void sendResponse(int code, int fullLength);

}
