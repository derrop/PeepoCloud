package net.peepocloud.addons.acp;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.*;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.utility.SystemUtils;

import java.nio.file.Path;

@Getter
public class ACPConfig {

    private final Path path;

    private String host;
    private int port;

    public ACPConfig(Path path) {
        this.path = path;
    }

    public ACPConfig load() {
        SystemUtils.createFile(this.path);
        YamlConfigurable configurable = YamlConfigurable.load(this.path);
        if (!configurable.contains("host")) {
            configurable.append("host", "*");
            configurable.saveAsFile(this.path);
        }
        if (!configurable.contains("port")) {
            configurable.append("port", 1383);
            configurable.saveAsFile(this.path);
        }
        this.host = configurable.getString("host");
        this.port = configurable.getInt("port");
        return this;
    }

    public void save() {
        new YamlConfigurable()
                .append("host", this.host)
                .append("port", this.port)
                .saveAsFile(this.path);
    }

}
