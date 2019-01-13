package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultRestAPIClient implements RestAPIClient {

    private DefaultRestAPIRequestBody requestBody;
    private final HttpExchange httpExchange;
    private final Map<String, String> query;
    @Getter
    private final RestAPIRequestMethod requestMethod;

    @Override
    public URI getRequestURI() {
        return this.httpExchange.getRequestURI();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.httpExchange.getRemoteAddress();
    }

    @Override
    public Map<String, String> getParsedRequestQuery() {
        return this.query;
    }

    @Override
    public String getRequestHeader(String key) {
        return this.httpExchange.getRequestHeaders().getFirst(key);
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return this.httpExchange.getRequestHeaders().entrySet().stream().filter(stringListEntry -> !stringListEntry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue().isEmpty() ? "" : o.getValue().get(0)));
    }

    @Override
    public void setResponseHeader(String key, String value) {
        this.httpExchange.getResponseHeaders().set(key, value);
    }

    @Override
    public RestAPIRequestBody getRequestBody() {
        return this.requestBody != null ? this.requestBody : (this.requestBody = new DefaultRestAPIRequestBody(this.httpExchange.getRequestBody()));
    }

    @Override
    public void sendResponse(int code, InputStream inputStream) {
        try {
            this.httpExchange.sendResponseHeaders(code, inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OutputStream body = this.httpExchange.getResponseBody();
            int b;
            while ((b = inputStream.read()) != -1) {
                body.write(b);
            }
            body.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendResponse(int code, byte[] bytes) {
        try {
            this.httpExchange.sendResponseHeaders(code, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream body = this.httpExchange.getResponseBody();
        try {
            body.write(bytes);
            body.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendResponse(int code, int fullLength) {
        try {
            this.httpExchange.sendResponseHeaders(code, fullLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
