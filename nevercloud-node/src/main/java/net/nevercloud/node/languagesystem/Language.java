package net.nevercloud.node.languagesystem;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import com.google.gson.JsonObject;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Language {
    private String name;
    private String shortName;
    private Map<String, String> messages;

    public static Language load(Properties properties) {
        Language language = new Language();
        language.name = properties.getProperty("name");
        language.shortName = properties.getProperty("shortName");
        language.messages = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = String.valueOf(entry.getKey());
            if (key.equals("name") || key.equalsIgnoreCase("shortName"))
                continue;
            language.messages.put(key, String.valueOf(entry.getValue()));
        }
        return language;
    }
}
