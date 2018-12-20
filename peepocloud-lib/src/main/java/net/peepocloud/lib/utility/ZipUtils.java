package net.peepocloud.lib.utility;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void zipDirectory(Path directory, Path targetFile) {
        try (OutputStream outputStream = Files.newOutputStream(targetFile, StandardOpenOption.CREATE_NEW)) {
            zipDirectory(directory, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] zipDirectory(Path directory) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        zipDirectory(directory, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void zipDirectory(Path directory, OutputStream outputStream) {
        if (!Files.exists(directory))
            return;
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            Files.walkFileTree(
                    directory,
                    EnumSet.noneOf(FileVisitOption.class),
                    Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            try {
                                zipOutputStream.putNextEntry(new ZipEntry(directory.relativize(file).toString()));
                                Files.copy(file, zipOutputStream);
                                zipOutputStream.closeEntry();
                            } catch (Exception e) {
                                e.printStackTrace();
                                zipOutputStream.closeEntry();
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzipDirectory(byte[] zippedBytes, String outputDirectory) {
        try (InputStream inputStream = new ByteArrayInputStream(zippedBytes)) {
            unzipDirectory(inputStream, outputDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzipDirectory(Path zippedFile, String outputDirectory) {
        if (!Files.exists(zippedFile))
            return;
        try (InputStream inputStream = Files.newInputStream(zippedFile)) {
            unzipDirectory(inputStream, outputDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzipDirectory(InputStream inputStream, String outputDirectory) {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            if (!Files.exists(Paths.get(outputDirectory))) {
                Files.createDirectories(Paths.get(outputDirectory));
            }

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                try {
                    Path path = Paths.get(outputDirectory, zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        if (!Files.exists(path)) {
                            Files.createDirectories(path);
                        }
                    } else {
                        Files.copy(zipInputStream, path, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zipInputStream.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
