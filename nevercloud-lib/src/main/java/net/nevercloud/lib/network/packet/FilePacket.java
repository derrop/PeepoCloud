package net.nevercloud.lib.network.packet;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import net.nevercloud.lib.utility.ZipUtils;
import net.nevercloud.lib.utility.network.PacketUtils;

import java.io.DataInput;
import java.io.DataOutput;
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
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeBoolean(this.directory);
        dataOutput.writeInt(this.bytes.length);
        PacketUtils.writeBytes(dataOutput, this.bytes);
    }

    @Override
    public void read(DataInput dataInput) throws IOException {
        this.directory = dataInput.readBoolean();
        this.bytes = PacketUtils.readBytes(dataInput);
    }
}
