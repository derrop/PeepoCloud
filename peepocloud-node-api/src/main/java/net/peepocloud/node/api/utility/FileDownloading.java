package net.peepocloud.node.api.utility;
/*
 * Created by Mc_Ruben on 24.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.logging.ConsoleLogger;
import net.peepocloud.node.api.logging.animated.ConsoleProgressBarAnimation;

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
    /**
     * Copies the content of the {@link InputStream} to the {@link OutputStream} with a {@link ConsoleProgressBarAnimation} in the specified {@link ConsoleLogger}
     *
     * @param logger       the logger to print the progress bar to
     * @param length       the full length of the {@link InputStream}
     * @param inputStream  the inputStream to copy from
     * @param outputStream the outputStream to copy to content of the inputStream to
     * @throws IOException if an I/O Error occurs
     */
    public static void copyStreamWithProgessBar(ConsoleLogger logger, int length, InputStream inputStream, OutputStream outputStream) throws IOException {
        ConsoleProgressBarAnimation animation = new ConsoleProgressBarAnimation(logger, length, 0, '=', '>', "<!", "!> %value% MB / %length% MB | %percent%%, %time% time elapsed, %bps% KBit/s") {
            @Override
            protected String formatCurrentValue(long currentValue) {
                return String.format("%.3f", (double) currentValue / 1000D / 1000D); //format to MB
            }

            @Override
            protected String formatLength(long length) {
                return this.formatCurrentValue(length);
            }
        };

        logger.startAnimation(animation);

        int read = 0;
        byte[] buf = new byte[16];
        int len;
        while ((len = inputStream.read(buf)) != -1) {
            read += len;
            animation.setCurrentValue(read);
            outputStream.write(buf, 0, len);
        }
        SystemUtils.sleepUninterruptedly(50);
    }

    /**
     * Downloads a file from the specified url to specified {@link Path} with a {@link ConsoleProgressBarAnimation} in the specified {@link ConsoleLogger}
     *
     * @param logger   the logger to print the progress bar to
     * @param url      the url to download the file from
     * @param path     the path to which the downloaded file is saved
     * @param finished called when the file was downloaded successfully
     * @param error    called when the file could not be downloaded successfully
     * @return {@code true} if it was downloaded successfully or {@code false} if not
     */
    public static boolean downloadFileWithProgressBar(ConsoleLogger logger, String url, Path path, Runnable finished, Runnable error) {
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

    /**
     * Downloads a file from the specified url to specified {@link OutputStream} with a {@link ConsoleProgressBarAnimation} in the specified {@link ConsoleLogger}
     *
     * @param logger       the logger to print the progress bar to
     * @param url          the url to download the file from
     * @param outputStream the outputStream to which the downloaded file is saved
     * @param finished     called when the file was downloaded successfully
     * @param error        called when the file could not be downloaded successfully
     * @return {@code true} if it was downloaded successfully or {@code false} if not
     */
    public static boolean downloadFileWithProgressBar(ConsoleLogger logger, String url, OutputStream outputStream, Runnable finished, Runnable error) {
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
