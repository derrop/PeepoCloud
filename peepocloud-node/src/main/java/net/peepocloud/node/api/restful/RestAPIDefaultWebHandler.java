package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RestAPIDefaultWebHandler implements HttpHandler {
    private RestAPIProviderImpl provider;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        Collection<RestAPIHandlerInfo> handlers = this.provider.handlers(path);
        if (handlers.isEmpty()) {
            byte[] bytes = this.provider._404();
            send(httpExchange, 404, bytes);
            return;
        }

        RestAPIRequestMethod method = RestAPIRequestMethod.getByName(httpExchange.getRequestMethod());
        if (method == null || handlers.stream().noneMatch(handler -> Arrays.asList(handler.getHandler().supportedMethods()).contains(method))) {
            byte[] bytes = this.provider._method_not_found(httpExchange.getRequestMethod(), handlers.stream().map(info -> info.getHandler().supportedMethods()).collect(Collectors.toList()));
            send(httpExchange, 400, bytes);
            return;
        }

        Map<String, String> query = parseQuery(httpExchange.getRequestURI().getQuery());

        boolean usingRateLimit = this.provider.isRateLimitEnabled();
        for (RestAPIHandlerInfo handler : handlers) {
            usingRateLimit = handler.getHandler().usesRateLimit();
        }

        if (usingRateLimit) {
            if (handlers.stream().anyMatch(info -> info.getHandler().usesRateLimit() && info.isRateLimited(httpExchange.getRemoteAddress().getHostString()))) {
                byte[] bytes = this.provider._rate_limit();
                send(httpExchange, 400, bytes);
                return;
            }

            if (!this.provider.getConnections().containsKey(httpExchange.getRemoteAddress().getHostString())) {
                this.provider.getConnections().put(httpExchange.getRemoteAddress().getHostString(), new ArrayList<>());
            }
            this.provider.getConnections().get(httpExchange.getRemoteAddress().getHostString()).add(System.currentTimeMillis());
            if (this.provider.getConnections().get(httpExchange.getRemoteAddress().getHostString()).stream().filter(connect -> System.currentTimeMillis() + 5000 > connect).count() > this.provider.getConnectionsToRateLimit()) {
                this.provider.getConnections().remove(httpExchange.getRemoteAddress().getHostString());
                for (RestAPIHandlerInfo handler : handlers) {
                    if (handler.getHandler().usesRateLimit()) {
                        handler.getRateLimits().put(httpExchange.getRemoteAddress().getHostString(), System.currentTimeMillis() + this.provider.getRateLimit());
                    }
                }
                byte[] bytes = this.provider._rate_limit();
                send(httpExchange, 400, bytes);
                return;
            }
        }


        boolean handled = false;
        Collection<String> missingHeaders = null;
        RestAPIClient client = new DefaultRestAPIClient(httpExchange, query, method);
        for (RestAPIHandlerInfo handler : handlers) {
            if (handler.getHandler().getRequiredHeaders() != null) {
                boolean headerMissing = false;
                for (String requiredHeader : handler.getHandler().getRequiredHeaders()) {
                    if (!httpExchange.getRequestHeaders().containsKey(requiredHeader)) {
                        if (missingHeaders == null)
                            missingHeaders = new ArrayList<>();
                        missingHeaders.add(requiredHeader);
                        headerMissing = true;
                    }
                }
                if (headerMissing)
                    continue;
            }
            handler.getHandler().handle(client);
            handled = true;
        }

        if (missingHeaders != null && !missingHeaders.isEmpty()) {
            byte[] bytes = ("The following requestHeaders are missing: " + String.join(", ", missingHeaders)).getBytes(StandardCharsets.UTF_8);
            send(httpExchange, 400, bytes);
        } else if (!handled) {
            byte[] bytes = this.provider._rate_limit();
            send(httpExchange, 400, bytes);
        }

    }

    private void send(HttpExchange httpExchange, int code, byte[] bytes) throws IOException {
        httpExchange.sendResponseHeaders(code, bytes.length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.close();
    }

    private static Map<String, String> parseQuery(String query) {
        if (query == null)
            return Collections.emptyMap();
        Map<String, String> map = new HashMap<>();
        for (String s : query.split("&")) {
            String[] args = s.split("=");
            if (args.length == 0)
                continue;
            if (args.length == 1) {
                map.put(args[0], null);
                continue;
            }
            if (args.length == 2) {
                map.put(args[0], args[1]);
                continue;
            }
            String val = s.substring(s.indexOf('=') + 1);
            map.put(args[0], val);
        }
        return map;
    }
}
