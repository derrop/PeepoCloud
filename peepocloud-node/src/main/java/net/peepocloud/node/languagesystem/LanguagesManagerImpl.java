package net.peepocloud.node.languagesystem;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import net.md_5.bungee.http.HttpClient;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.languagesystem.Language;
import net.peepocloud.node.api.languagesystem.LanguagesManager;

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
public class LanguagesManagerImpl extends LanguagesManager {

    private Language defaultLanguage;
    private String languageName;
    private Language selectedLanguage;


    public LanguagesManagerImpl() {
        try {
            this.loadDefaultLanguage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String selectedLang = PeepoCloudNode.getInstance().getInternalConfig().getString("language");
        if (selectedLang != null) {
            this.setSelectedLanguage(selectedLang, language -> {
                if (language == null) {
                    System.out.println("&cSelected language &e" + selectedLang + " &ccouldn't be loaded, using default language &e" + this.defaultLanguage.getName() + " (" + this.defaultLanguage.getShortName() + ")");
                } else {
                    System.out.println("&aSuccessfully loaded language &e" + language.getName() + " (" + language.getShortName() + ") &awith &e" + language.getMessages().size() + " messages");
                }
            });
        }
    }

    private void setLang(Language lang) {
        if (lang == null)
            return;

        this.languageName = lang.getName();
        this.selectedLanguage = lang;
        PeepoCloudNode.getInstance().getInternalConfig().append("language", lang.getName());
        PeepoCloudNode.getInstance().saveInternalConfigFile();
    }

    private void loadDefaultLanguage() throws IOException {
        Properties properties = new Properties();
        properties.load(LanguagesManagerImpl.class.getClassLoader().getResourceAsStream("languages/default-language-en_US.properties"));
        this.defaultLanguage = Language.load(properties);
    }

    @Override
    public String getSelectedLanguageName() {
        return this.languageName;
    }

    @Override
    public void getAvailableLanguages(Consumer<Collection<String>> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "languages?all=response", null, (s, throwable) -> {
            if (throwable == null) {
                Collection<String> languages = SimpleJsonObject.GSON.get().fromJson(s, new TypeToken<Collection<String>>() {
                }.getType());
                consumer.accept(languages);
            } else {
                consumer.accept(null);
            }
        });
    }

    @Override
    public Collection<String> getAvailableLanguages() {
        Collection<String> languages = Collections.emptyList();
        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "languages?all=response").openConnection();
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream)) {
                languages = SimpleJsonObject.GSON.get().fromJson(reader, new TypeToken<Collection<String>>() {
                }.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return languages;
    }

    @Override
    public void setSelectedLanguage(String name, Consumer<Language> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "languages?name=" + name, null, (s, throwable) -> {
            if (throwable == null) {
                Language language = loadLanguageFromJson(s);
                setLang(language);
                consumer.accept(language);
            } else {
                consumer.accept(null);
            }
        });
    }

    @Override
    public void setSelectedLanguageByShortName(String shortName, Consumer<Language> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "languages?shortName=" + shortName, null, (s, throwable) -> {
            if (throwable == null) {
                Language language = loadLanguageFromJson(s);
                setLang(language);
                consumer.accept(language);
            } else {
                consumer.accept(null);
            }
        });
    }

    private Language loadLanguageFromJson(String s) {
        JsonElement jsonElement = SimpleJsonObject.PARSER.parse(s);
        if (jsonElement == null || !jsonElement.isJsonObject())
            return null;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.get("success").getAsBoolean())
            return null;
        return SimpleJsonObject.GSON.get().fromJson(jsonObject.get("response"), Language.class);
    }

    /**
     * Gets the selected {@link Language} of this LanguagesManagerImpl
     * @return the {@link Language} of this LanguagesManagerImpl or the defaultLanguage if no {@link Language} is set
     */
    public Language getLanguage() {
        if (selectedLanguage == null)
            return defaultLanguage;
        return selectedLanguage;
    }

    /**
     * Gets a message out of the {@link Language} selected in this LanguagesManagerImpl
     * @param key the key of the message
     * @return the message out of the {@link Language} by the specified key
     */
    public String getMessage(String key) {
        Language language = getLanguage();
        return language.getMessages().getOrDefault(key, "<key \"" + key + "\" was not found in the language \"" + language.getName() + "\">");
    }


}
