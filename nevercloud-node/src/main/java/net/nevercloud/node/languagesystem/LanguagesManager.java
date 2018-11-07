package net.nevercloud.node.languagesystem;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.*;
import net.md_5.bungee.http.HttpClient;
import net.nevercloud.lib.json.SimpleJsonObject;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

@Getter
public class LanguagesManager {

    private Language defaultLanguage;
    private String languageName;
    private Language selectedLanguage;


    public LanguagesManager() {
        try {
            this.loadDefaultLanguage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String selectedLang = NeverCloudNode.getInstance().getInternalConfig().getString("language");
        if (selectedLang != null) {
            this.setSelectedLanguage(selectedLang, language -> {
                if (language == null) {
                    System.out.println("&cSelected language &e" + selectedLang + " &ccouldn't be loaded");
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
        NeverCloudNode.getInstance().getInternalConfig().append("language", lang.getName());
        NeverCloudNode.getInstance().saveInternalConfigFile();
    }

    private void loadDefaultLanguage() throws IOException {
        Properties properties = new Properties();
        properties.load(LanguagesManager.class.getClassLoader().getResourceAsStream("languages/default-language-en_US.properties"));
        this.defaultLanguage = Language.load(properties);
    }

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
        return SimpleJsonObject.GSON.fromJson(jsonObject.get("response"), Language.class);
    }

    public Language getLanguage() {
        if (selectedLanguage == null)
            return defaultLanguage;
        return selectedLanguage;
    }

    public String getMessage(String key) {
        Language language = getLanguage();
        return language.getMessages().getOrDefault(key, "<key \"" + key + "\" was not found in the language \"" + language.getName() + "\">");
    }


}
