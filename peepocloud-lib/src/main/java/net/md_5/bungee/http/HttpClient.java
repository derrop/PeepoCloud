package net.md_5.bungee.http;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.peepocloud.lib.utility.Callback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

/*
  This code has been taken from BungeeCord by md_5 (https://github.com/SpigotMC/BungeeCord)
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpClient {

    public static final int TIMEOUT = 5000;
    private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    public static void get(String url, EventLoopGroup eventLoop, final Callback<String> callback) {
        Preconditions.checkNotNull(url, "url");
        Preconditions.checkNotNull(callback, "callBack");

        if (eventLoop == null) {
            eventLoop = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        }

        final URI uri = URI.create(url);

        Preconditions.checkNotNull(uri.getScheme(), "scheme");
        Preconditions.checkNotNull(uri.getHost(), "host");
        boolean ssl = uri.getScheme().equals("https");
        int port = uri.getPort();
        if (port == -1) {
            switch (uri.getScheme()) {
                case "http":
                    port = 80;
                    break;
                case "https":
                    port = 443;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
            }
        }

        InetAddress inetHost = addressCache.getIfPresent(uri.getHost());
        if (inetHost == null) {
            try {
                inetHost = InetAddress.getByName(uri.getHost());
            } catch (UnknownHostException ex) {
                callback.done(null, ex);
                return;
            }
            addressCache.put(uri.getHost(), inetHost);
        }

        ChannelFutureListener future = future1 -> {
            if (future1.isSuccess()) {
                String path = uri.getRawPath() + ((uri.getRawQuery() == null) ? "" : "?" + uri.getRawQuery());

                HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
                request.headers().set(HttpHeaders.Names.HOST, uri.getHost());

                future1.channel().writeAndFlush(request);
            } else {
                addressCache.invalidate(uri.getHost());
                callback.done(null, future1.cause());
            }
        };

        new Bootstrap().channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class).group(eventLoop)
                .handler(new HttpInitializer(callback, ssl, uri.getHost(), port)).
                option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT).remoteAddress(inetHost, port).connect().addListener(future);
    }

    public static boolean downloadFile(String url, Path targetPath) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setConnectTimeout(TIMEOUT);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            Path parent = targetPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            OutputStream outputStream = Files.newOutputStream(targetPath, StandardOpenOption.CREATE_NEW);

            byte[] buf = new byte[512];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }

            outputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] downloadFile(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setConnectTimeout(TIMEOUT);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buf = new byte[512];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                byteArrayOutputStream.write(buf, 0, len);
            }

            inputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
