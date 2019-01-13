package net.peepocloud.node.api.restful.handler.file;
/*
 * Created by Mc_Ruben on 13.01.2019
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class DirectoryWebsiteRestAPIHandler extends WebsiteRestAPIHandler {

    private String directory;

    public DirectoryWebsiteRestAPIHandler(boolean useCaches, String directory) {
        super(useCaches);
        this.directory = directory;
    }

    @Override
    public byte[] getBytesForFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(this.directory, path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean isDirectory(String path) {
        return Files.isDirectory(Paths.get(this.directory, path));
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(Paths.get(this.directory, path));
    }

}
