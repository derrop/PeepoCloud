package net.peepocloud.addons.templates.ftp;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class FtpClient implements AutoCloseable {

    private Session session;
    private ChannelSftp channel;

    public boolean connect(String host, String username, String password, int port) {
        System.out.println("Trying to connect to sftp...");
        try {
            this.session = new JSch().getSession(username, host, port);
            this.session.setPassword(password);
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect(2500);
        } catch (JSchException e) {
            e.printStackTrace();
            System.out.println("&cThere was an error while trying to connect to sftp-server @" + host + ":" + port);
            return false;
        }

        try {
            this.channel = (ChannelSftp) session.openChannel("sftp");
            if (this.channel == null) {
                close();
                System.out.println("&cThere was an error while opening the sftp-session with the user " + username);
                return false;
            }
            this.channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            System.out.println("&cThere was an error while opening the sftp-session with the user " + username);
            return false;
        }

        if (this.isConnected()) {
            System.out.println("&aSuccessfully connected to sftp @" + host + ":" + port);
        }

        return this.isConnected();
    }

    public boolean isConnected() {
        return session != null && session.isConnected() && channel != null && channel.isConnected();
    }

    @Override
    public void close() {
        try {
            if (channel != null) {
                channel.disconnect();
                channel = null;
            }
        } finally {
            if (session != null) {
                session.disconnect();
                session = null;
            }
        }
    }

    public void createFile(String remotePath) {
        try {
            this.channel.put(remotePath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public void createDirectories(String remotePath) {
        StringBuilder builder = new StringBuilder();
        for (String s : remotePath.split("/")) {
            builder.append('/').append(s);
            try {
                channel.mkdir(builder.toString());
            } catch (SftpException e) {
                //dir already exists
            }
        }
    }

    public void uploadFile(String localPath, String remotePath) {
        try {
            this.channel.put(localPath, remotePath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(Path localPath, String remotePath) {
        if (!Files.exists(localPath))
            return;
        try (InputStream inputStream = Files.newInputStream(localPath)) {
            this.uploadFile(inputStream, remotePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(InputStream inputStream, String remotePath) {
        try {
            this.channel.put(inputStream, remotePath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String remotePath, String localPath) {
        try {
            channel.get(remotePath, localPath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public boolean existsDirectory(String path) {
        try {
            SftpATTRS attrs = this.channel.stat(path);
            return attrs != null && (attrs.isDir());
        } catch (SftpException e) {
            return false;
        }
    }

    public void downloadDirectory(String remotePath, String localPath) {
        if (!remotePath.endsWith("/"))
            remotePath += "/";

        try {
            Collection<ChannelSftp.LsEntry> entries = this.listFiles(remotePath);

            Path dir = Paths.get(localPath);
            if (Files.exists(dir)) {
                //TODO delete using SystemUtils.deleteDirectory
            }
            Files.createDirectories(dir);

            for (ChannelSftp.LsEntry entry : entries) {
                if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                    continue;
                if (!entry.getAttrs().isDir() && !entry.getAttrs().isLink()) {
                    try (OutputStream outputStream = Files.newOutputStream(Paths.get(localPath, entry.getFilename()))) {
                        channel.get(remotePath + entry.getFilename(), outputStream);
                    }
                } else if (entry.getAttrs().isDir()) {
                    this.downloadDirectory(remotePath + "/" + entry.getFilename(), localPath + "/" + entry.getFilename());
                }
            }

        } catch (SftpException | IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadDirectory(Path localPath, String remotePath) {
        try {
            this.createDirectories(remotePath);

            Files.walkFileTree(
                    localPath,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            String path = remotePath + "/" + localPath.relativize(dir).toString();
                            try {
                                channel.mkdir(path);
                            } catch (SftpException e) {
                                //dir already exists
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Path parent = file.getParent();
                            String path = parent == null ? remotePath : remotePath + "/" + parent.relativize(localPath).toString();
                            path = path.replaceAll("/\\.\\.", "");
                            try {
                                channel.put(file.toString(), path);
                            } catch (SftpException e) {
                                e.printStackTrace();
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDirectory(String path) {
        try {
            Collection<ChannelSftp.LsEntry> entries = this.listFiles(path);

            for (ChannelSftp.LsEntry entry : entries) {
                if (entry.getAttrs().isDir()) {
                    this.deleteDirectory(path + "/" + entry.getFilename());
                } else {
                    try {
                        this.channel.rm(path + "/" + entry.getFilename());
                    } catch (SftpException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.channel.rmdir(path);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public Collection<ChannelSftp.LsEntry> listFiles(String directory) {
        Collection<ChannelSftp.LsEntry> entries = new ArrayList<>();
        try {
            channel.ls(directory, lsEntry -> {
                if (!lsEntry.getFilename().equals("..") && !lsEntry.getFilename().equals(".")) {
                    entries.add(lsEntry);
                }
                return 0;
            });
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return entries;
    }

}
