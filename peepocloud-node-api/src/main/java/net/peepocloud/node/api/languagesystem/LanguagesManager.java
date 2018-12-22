package net.peepocloud.node.api.languagesystem;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.md_5.bungee.http.HttpClient;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

@Getter
public abstract class LanguagesManager {


    public abstract Language getDefaultLanguage();

    public abstract String getSelectedLanguageName();

    public abstract Language getSelectedLanguage();

    /**
     * Loads the names of all available languages asynchronously from the server and posts them to the specified {@link Consumer}
     * @param consumer the consumer to accept the names of all available languages
     */
    public abstract void getAvailableLanguages(Consumer<Collection<String>> consumer);

    /**
     * Loads the names of all available languages synchronously from the server
     * @return all available languages
     */
    public abstract Collection<String> getAvailableLanguages();

    /**
     * Sets the {@link Language} of this LanguagesManager by the name of the language
     * @param name the name of the language
     * @param consumer will be accepted with {@link Language} if the language was found on the server and successfully set or with {@code null} if the the language was not found
     */
    public abstract void setSelectedLanguage(String name, Consumer<Language> consumer);

    /**
     * Sets the {@link Language} of this LanguagesManager by the short name of the language
     * @param shortName the short name of the language
     * @param consumer will be accepted with {@link Language} if the language was found on the server and successfully set or with {@code null} if the the language was not found
     */
    public abstract void setSelectedLanguageByShortName(String shortName, Consumer<Language> consumer);

    /**
     * Gets the selected {@link Language} of this LanguagesManager
     * @return the {@link Language} of this LanguagesManager or the defaultLanguage if no {@link Language} is set
     */
    public abstract Language getLanguage();

    /**
     * Gets a message out of the {@link Language} selected in this LanguagesManager
     * @param key the key of the message
     * @return the message out of the {@link Language} by the specified key
     */
    public abstract String getMessage(String key);


}
