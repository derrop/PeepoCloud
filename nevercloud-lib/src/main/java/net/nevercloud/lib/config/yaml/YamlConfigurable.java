package net.nevercloud.lib.config.yaml;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.nevercloud.lib.config.Configurable;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class YamlConfigurable implements Configurable<YamlConfigurable> {

    private static ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Configuration configuration;
    
    public YamlConfigurable() { }

    public YamlConfigurable(Reader reader) {
        this.configuration = configurationProvider.load(reader);
    }

    public YamlConfigurable(String input) {
        this.configuration = configurationProvider.load(input);
    }
    
    @Override
    public YamlConfigurable append(String key, Object value) {
        this.configuration.set(key, value);
        return this;
    }

    @Override
    public YamlConfigurable append(String key, String value) {
        this.configuration.set(key, value);
        return this;
    }

    @Override
    public YamlConfigurable append(String key, Character value) {
        this.configuration.set(key, value);
        return this;
    }

    @Override
    public YamlConfigurable append(String key, Boolean value) {
        this.configuration.set(key, value);
        return this;
    }

    @Override
    public YamlConfigurable append(String key, Number value) {
        this.configuration.set(key, value);
        return this;
    }

    @Override
    public boolean contains(String key) {
        return this.configuration.contains(key);
    }

    @Override
    public String getString(String key) {
        return this.configuration.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return this.configuration.getBoolean(key);
    }

    @Override
    public char getCharacter(String key) {
        return this.configuration.getChar(key);
    }

    @Override
    public byte getByte(String key) {
        return this.configuration.getByte(key);
    }

    @Override
    public short getShort(String key) {
        return this.configuration.getShort(key);
    }

    @Override
    public int getInt(String key) {
        return this.configuration.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return this.configuration.getLong(key);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getObject(String key, Class<T> tClass) {
        return (T) this.configuration.get(key);
    }

    @Override
    public Object getObject(String key, Type type) {
        return this.configuration.get(key);
    }

    public Object get(String key) {
        return this.configuration.get(key);
    }

    public Configuration asConfiguration() {
        return this.configuration;
    }

    @Override
    public void saveAsFile(Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
             Writer writer = new OutputStreamWriter(outputStream)) {
            configurationProvider.save(this.configuration, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] toBytes() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        try (StringWriter writer = new StringWriter()) {
            configurationProvider.save(this.configuration, writer);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static YamlConfigurable load(String path) {
        return load(Paths.get(path));
    }

    public static YamlConfigurable load(Path path) {
        if (!Files.exists(path))
            return new YamlConfigurable();
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            return new YamlConfigurable(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new YamlConfigurable();
    }
}
