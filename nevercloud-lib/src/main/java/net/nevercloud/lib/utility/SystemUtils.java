package net.nevercloud.lib.utility;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import lombok.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

public class SystemUtils {

    private SystemUtils() { }

    public static final String CENTRAL_SERVER_URL = "http://localhost:1350/";

    private static final char[] values = "abcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ1234567890".toCharArray();

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



}
