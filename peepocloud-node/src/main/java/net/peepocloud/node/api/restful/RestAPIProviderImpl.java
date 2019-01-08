package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import com.google.common.base.Preconditions;
import com.sun.net.httpserver.HttpServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.addon.Addon;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RestAPIProviderImpl implements RestAPIProvider, AutoCloseable {

    private Collection<RestAPIHandlerInfo> handlers = new ArrayList<>();

    private HttpServer httpServer;
    private boolean enabled;
    @Getter
    private boolean rateLimitEnabled;
    @Getter
    private long rateLimit, connectionsToRateLimit;
    private Thread rateLimitUpdater;
    @Getter
    private Map<String, Collection<Long>> connections = new HashMap<>();

    @Override
    public boolean isRunning() {
        return this.httpServer != null && this.enabled;
    }

    @Override
    public InetSocketAddress getHostAddress() {
        return this.httpServer != null ? this.httpServer.getAddress() : null;
    }

    @Override
    public void registerAPIHandler(Addon addon, RestAPIHandler handler) {
        Preconditions.checkNotNull(addon, "addon cannot be null");
        this.handlers.add(new RestAPIHandlerInfo(addon, handler, new HashMap<>()));
    }

    @Override
    public void unregisterAPIHandlers(Addon addon) {
        this.handlers.stream().filter(info -> info.getAddon() != null && info.getAddon().getAddonConfig().getName().equals(addon.getAddonConfig().getName())).collect(Collectors.toList())
                .forEach(info -> this.handlers.remove(info));
    }

    public void registerAPIHandlerInternal(RestAPIHandler handler) {
        this.handlers.add(new RestAPIHandlerInfo(null, handler, new HashMap<>()));
    }

    public void doBind(InetSocketAddress address) {
        if (this.enabled) {
            try {
                this.httpServer = HttpServer.create(address, 0);
                this.httpServer.createContext("/", new RestAPIDefaultWebHandler(this));
                this.httpServer.start();

                System.out.println("&aBound RestAPI HttpServer @" + address.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        if (this.httpServer != null) {
            this.httpServer.stop(0);
            this.httpServer = null;
        }
    }

    public void load(boolean enabled, boolean rateLimitEnabled, long connectionsToRateLimit, long rateLimit, InetSocketAddress address) {
        if (this.rateLimitEnabled != rateLimitEnabled) {
            if (rateLimitEnabled && (this.rateLimitUpdater == null || this.rateLimitUpdater.isInterrupted())) {
                this.rateLimitUpdater = new Thread(() -> {
                    while (!Thread.interrupted()) {
                        for (RestAPIHandlerInfo handler : this.handlers) {
                            if (!handler.getRateLimits().isEmpty()) {
                                handler.getRateLimits().entrySet().stream()
                                        .filter(stringLongEntry -> stringLongEntry.getValue() >= System.currentTimeMillis()).collect(Collectors.toList())
                                        .forEach(stringLongEntry -> handler.getRateLimits().remove(stringLongEntry.getKey(), stringLongEntry.getValue()));
                            }
                        }
                        SystemUtils.sleepUninterruptedly(5000);
                    }
                }, "RestAPI RateLimit Updater");
            } else if (!rateLimitEnabled && this.rateLimitUpdater != null) {
                this.rateLimitUpdater.interrupt();
                this.rateLimitUpdater = null;
            }
        }

        this.rateLimitEnabled = rateLimitEnabled;
        this.rateLimit = rateLimit;
        this.connectionsToRateLimit = connectionsToRateLimit;

        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.enabled) {
                this.doBind(address);
            } else {
                this.close();
            }
            return;
        }
        if (this.getHostAddress() != null && this.getHostAddress().equals(address))
            return;

        this.close();
        SystemUtils.sleepUninterruptedly(50);
        this.doBind(address);
    }

    Collection<RestAPIHandlerInfo> handlers(String requestPath) {
        return this.handlers.stream()
                .filter(info -> {
                    String path = info.getHandler().getPath();
                    return isGlobal(path) || isPathAccepted(path, requestPath);
                }).collect(Collectors.toList());
    }

    byte[] _404() {
        return "_404_".getBytes(StandardCharsets.UTF_8); //TODO make configurable
    }

    byte[] _method_not_found(String method, Collection<RestAPIRequestMethod[]> supportedMethods) {
        return ("method \"" + method + "\" is invalid, must be one of the following: " +
                supportedMethods.stream().map(methods -> Arrays.stream(methods).map(RestAPIRequestMethod::name).collect(Collectors.joining(", "))).collect(Collectors.joining(", "))).getBytes(StandardCharsets.UTF_8);
    }

    byte[] _rate_limit() {
        return "you're ratelimited".getBytes(StandardCharsets.UTF_8);
    }

    private static boolean isGlobal(String path) {
        return path == null || path.equals("/");
    }

    private static boolean isPathAccepted(String path, String requestPath) {
        return requestPath != null && requestPath.equalsIgnoreCase(path);
    }

}
