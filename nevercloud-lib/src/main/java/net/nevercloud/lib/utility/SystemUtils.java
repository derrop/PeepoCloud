package net.nevercloud.lib.utility;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.nevercloud.lib.INeverCloudAPI;
import net.nevercloud.lib.network.packet.FilePacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SystemUtils {

    private SystemUtils() { }

    public static final String CENTRAL_SERVER_URL = "http://localhost:1350/";

    private static final char[] values = "abcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ1234567890".toCharArray();

    @Getter
    private static INeverCloudAPI api;

    public static void setApi(INeverCloudAPI api) {
        Preconditions.checkArgument(SystemUtils.api == null, "api is already initialized");
        SystemUtils.api = api;
    }

    public static String getCurrentVersion() {
        return "1.0";
    }

    public static String getPathOfInternalJarFile() {
        String name = SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (name.contains("/")) {
            String[] split = name.split("/");
            name = split[split.length - 1];
        }
        return name;
    }

    public static String randomString(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = values[ThreadLocalRandom.current().nextInt(values.length)];
        }
        return new String(chars);
    }

    public static byte[] readResource(ClassLoader classLoader, String path) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        byte[] buf = new byte[512];
        int len;
        while ((len = inputStream.read(buf)) != -1) {
            byteArrayOutputStream.write(buf, 0, len);
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void sleepUninterruptedly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public static void sleepUninterruptedly(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
        }
    }

    public static void copyDirectory(Path directory, String targetDirectory) {
        if (!Files.exists(directory))
            return;
        try {
            Files.walkFileTree(
                    directory,
                    EnumSet.noneOf(FileVisitOption.class),
                    Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Path target = Paths.get(targetDirectory, directory.relativize(file).toString());
                            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getFullLength(URLConnection connection) {
        String field = connection.getHeaderField("Content-Length");
        if (field == null || !isInteger(field))
            return -1;
        return Integer.parseInt(field);
    }

    public static int downloadFile(String url, OutputStream outputStream, Consumer<Integer> bytesLoaded, Runnable finished) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setConnectTimeout(2000);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            int length = getFullLength(connection);
            new Thread(() -> {
                int read = 0;
                try {
                    byte[] buf = new byte[128];
                    int len;
                    while ((len = inputStream.read(buf)) != -1) {
                        read += len;
                        if (bytesLoaded != null) {
                            bytesLoaded.accept(read);
                        }
                        outputStream.write(buf, 0, len);
                        if (read % 1000 == 0) {
                            outputStream.flush();
                        }
                    }
                    if (finished != null) {
                        finished.run();
                    }
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (finished != null) {
                        finished.run();
                    }
                }
            }).start();
            return length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int downloadFile(String url, Path path, Consumer<Integer> bytesLoaded, Runnable finished) {
        try {
            OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
            return downloadFile(url, outputStream, bytesLoaded, () -> {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finished.run();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
