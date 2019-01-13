package net.peepocloud.node.api.restful.handler.file;
/*
 * Created by Mc_Ruben on 13.01.2019
 */

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import net.peepocloud.node.api.restful.RestAPIClient;
import net.peepocloud.node.api.restful.RestAPIRequestMethod;
import net.peepocloud.node.api.restful.handler.RestAPIHandler;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class WebsiteRestAPIHandler implements RestAPIHandler {

    public WebsiteRestAPIHandler(boolean useCaches) {
        if (useCaches) {
            this.cache = CacheBuilder.newBuilder()
                    .expireAfterWrite(2, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, byte[]>() {
                        @Override
                        public byte[] load(String s) {
                            return getBytesForFile(s);
                        }
                    });
        }
    }

    @Getter
    private LoadingCache<String, byte[]> cache;

    @Override
    public Collection<String> getRequiredHeaders() {
        return null;
    }

    @Override
    public RestAPIRequestMethod[] supportedMethods() {
        return new RestAPIRequestMethod[]{RestAPIRequestMethod.GET};
    }

    @Override
    public void handle(RestAPIClient client) {
        URI uri = client.getRequestURI();
        String path = uri.getPath();
        if (path == null || path.contains("../") || !exists(path)) {
            client.sendResponse(404, get404Response(client));
            return;
        }
        path = path.substring(1);
        boolean a = false;
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
            a = true;
        }
        if (path.isEmpty()) {
            a = true;
        }
        if (isDirectory(path)) {
            if (a) {
                path += "/index.html";
                if (!exists(path) || isDirectory(path)) {
                    client.sendResponse(404, get404Response(client));
                    return;
                }
            } else {
                String host = client.getRequestHeader("Host");
                if (host != null) {
                    String url = "http://" + host + "/" + (path + "/");
                    client.sendResponse(
                            200,
                            ("<head><meta http-equiv=\"refresh\" content=\"0; URL=" + url + "\"></head><body></body>").getBytes(StandardCharsets.UTF_8)
                    );
                    return;
                }
            }
        }

        client.setResponseHeader("Content-Type", this.getType(path));
        client.sendResponse(200, this.getBytes(path));
    }

    protected byte[] getBytes(String path) {
        if (this.cache != null) {
            try {
                return this.cache.get(path);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return this.getBytesForFile(path);
    }

    protected String getType(String path) {
        String contentType = "text/plain";
        int indexOf = path.lastIndexOf('.');
        if (indexOf > 0 && path.length() > indexOf) {
            String end = path.substring(indexOf + 1);
            String mimeType = MimeTypes.getMimeType(end);
            if (mimeType != null) {
                contentType = mimeType;
            }
        }
        return contentType;
    }

    public abstract byte[] getBytesForFile(String path);

    public abstract boolean isDirectory(String path);

    public abstract boolean exists(String path);

    public abstract byte[] get404Response(RestAPIClient client);

}
