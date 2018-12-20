package net.peepocloud.api.users;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.*;
import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.commons.utility.SystemUtils;

@Data
@ToString
@EqualsAndHashCode
public class User {

    public User(String username, String password, String apiToken) {
        this.username = username;
        this.hashedPassword = SystemUtils.hashString(password);
        this.apiToken = apiToken;
        this.metaData = new SimpleJsonObject();
    }

    private String username;
    private String hashedPassword;
    private String apiToken;
    private SimpleJsonObject metaData;
}
