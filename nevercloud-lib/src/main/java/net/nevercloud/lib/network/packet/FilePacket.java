package net.nevercloud.lib.network.packet;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.buffer.ByteBuf;
import net.nevercloud.lib.utility.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FilePacket extends Packet {

    private boolean directory = false;
    private byte[] bytes;

    public FilePacket(int id) {
        super(id);
    }

    public FilePacket(int id, File file) {
        this(id, file.toPath());
    }

    public FilePacket(int id, String filePath) {
        this(id, Paths.get(filePath));
    }

    public FilePacket(int id, Path path) {
        super(id);
        if (!Files.exists(path)) {
            this.bytes = new byte[0];
            return;
        }
        if (Files.isDirectory(path)) {
            this.bytes = ZipUtils.zipDirectory(path);
            this.directory = true;
        } else {
            try {
                this.bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FilePacket(int id, byte[] bytes, boolean isDirectory) {
        super(id);
        this.bytes = bytes;
        this.directory = isDirectory;
    }

    public void writeFile(String outputPath) {
        if (this.directory) {
            ZipUtils.unzipDirectory(this.bytes, outputPath);
        } else {
            Path path = Paths.get(outputPath);
            if (Files.exists(path) && Files.isDirectory(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Path parent = path.getParent();
                if (parent != null && !Files.exists(parent)) {
                    try {
                        Files.createDirectories(parent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Files.write(path, this.bytes, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeBoolean(this.directory);
        byteBuf.writeInt(this.bytes.length);
        byteBuf.writeBytes(this.bytes);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.directory = byteBuf.readBoolean();
        this.bytes = new byte[byteBuf.readInt()];
        byteBuf.readBytes(this.bytes);
    }
}
