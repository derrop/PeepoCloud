package net.nevercloud.node.utility;
/*
 * Created by Mc_Ruben on 24.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.logging.animated.ConsoleProgressBarAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FileDownloading {
    public static void copyStreamWithProgessBar(ColoredLogger logger, int length, InputStream inputStream, OutputStream outputStream) throws IOException {
        ConsoleProgressBarAnimation animation = new ConsoleProgressBarAnimation(logger, length, 0, '=', '>', "<!", "!> %value% bytes / %length% bytes | %percent%%, %time% seconds, %bps% KBit/s");

        logger.startAnimation(animation);

        int read = 0;
        byte[] buf = new byte[128];
        int len;
        while ((len = inputStream.read(buf)) != -1) {
            read += len;
            animation.setCurrentValue(read);
            outputStream.write(buf, 0, len);
        }
        SystemUtils.sleepUninterruptedly(50);
    }

    public static boolean downloadFileWithProgressBar(ColoredLogger logger, String url, Path path, Runnable finished, Runnable error) {
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            return downloadFileWithProgressBar(logger, url, outputStream, finished, () -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                error.run();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean downloadFileWithProgressBar(ColoredLogger logger, String url, OutputStream outputStream, Runnable finished, Runnable error) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setUseCaches(false);
            connection.setConnectTimeout(2000);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            int length = SystemUtils.getFullLength(connection);

            if (length == -1) {
                if (error != null) {
                    error.run();
                }
                return false;
            }

            try {
                copyStreamWithProgessBar(logger, length, inputStream, outputStream);
                if (finished != null) {
                    finished.run();
                }
                inputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                if (error != null) {
                    error.run();
                }
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
