package net.peepocloud.lib.utility;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.io.FileUtils;
import sun.reflect.ReflectionFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class SystemUtils {

    private SystemUtils() { }

    public static final String CENTRAL_SERVER_URL = "http://derrupen.ddns.net:1350/";
    public static final String CENTRAL_SERVER_URL_WS_GSTATS = "ws://derrupen.ddns.net:1351";

    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy-mm:hh");
    public static final DateFormat DEFAULT_FILE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy-mm_hh");

    private static final char[] values = "abcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ1234567890".toCharArray();


    public static String getCurrentVersion() {
        return SystemUtils.class.getPackage().getImplementationVersion();
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

    public static double cpuUsageProcess() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad();
    }

    public static double cpuUsageSystem() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad();
    }

    public static long memoryUsageProcess() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }

    public static long memoryUsageSystem() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() -
                ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
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
                            Path parent = target.getParent();
                            if (parent != null && !Files.exists(parent))
                                Files.createDirectories(parent);
                            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(Path directory) {
        if (!Files.exists(directory))
            return;
        try {
            FileUtils.deleteDirectory(directory.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            Files.walkFileTree(
                    directory,
                    EnumSet.noneOf(FileVisitOption.class),
                    Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.deleteIfExists(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.deleteIfExists(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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

    public static boolean downloadFileSynchronized(String url, Path path) {
        try {
            createParent(path);
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setConnectTimeout(2000);
            connection.connect();

            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, path);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isServerOffline(Exception e) {
        return (e instanceof ConnectException && (e.getMessage().equals("Connection refused: connect") || e.getMessage().equals("Connection timed out: connect"))) ||
                (e instanceof SocketTimeoutException && (e.getMessage().equals("connect timed out")));
    }

    public static String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    public static int getAvailableCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Hashes the specified {@link String}
     * @param input the {@link String} to hash
     * @return the hashed {@link String} or if an error occurs the input {@link String}
     */
    public static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(input.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getMimeEncoder().encode(digest.digest()), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return input;
    }

    public static void createFile(Path path) {
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                    Files.createFile(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createParent(Path path) {
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Constructor createConstructorForSerialization(Class<?> clazz) throws NoSuchMethodException {
        return ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor());
    }

}
