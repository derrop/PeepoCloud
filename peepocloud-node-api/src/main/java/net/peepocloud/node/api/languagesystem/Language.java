package net.peepocloud.node.api.languagesystem;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Language {
    /**
     * The name of this Language
     */
    private String name;
    /**
     * The short name of this Language
     */
    private String shortName;
    /**
     * All the messages in this Language by their key
     */
    private Map<String, String> messages;

    /**
     * Loads a language out of the specified {@link Properties}
     * @param properties the properties to load the language from
     * @return a new Language with the name, shortName and messages out of the specified {@link Properties}
     */
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
