package net.peepocloud.lib.config.yaml;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.peepocloud.lib.config.Configurable;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class YamlConfigurable implements Configurable<YamlConfigurable> {

    private static ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Configuration configuration;
    
    public YamlConfigurable() {
        this.configuration = new Configuration();
    }

    public YamlConfigurable(Configuration configuration) {
        this.configuration = configuration;
    }

    public YamlConfigurable(Reader reader) {
        try {
            this.configuration = configurationProvider.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
            this.configuration = new Configuration();
        }
    }

    public YamlConfigurable(String input) {
        try {
            this.configuration = configurationProvider.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            this.configuration = new Configuration();
        }
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
        return this.configuration.getBoolean(key, false);
    }

    @Override
    public char getCharacter(String key) {
        return this.configuration.getChar(key, (char) -1);
    }

    @Override
    public byte getByte(String key) {
        return this.configuration.getByte(key, (byte) -1);
    }

    @Override
    public short getShort(String key) {
        return this.configuration.getShort(key, (short) -1);
    }

    @Override
    public int getInt(String key) {
        return this.configuration.getInt(key, -1);
    }

    @Override
    public long getLong(String key) {
        return this.configuration.getLong(key, -1L);
    }

    @Override
    public double getDouble(String key) {
        return this.configuration.getDouble(key, -1D);
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
    public <T> T getObject(String key, Type type) {
        return (T) this.configuration.get(key);
    }

    public Object get(String key) {
        return this.configuration.get(key);
    }

    public Configuration getConfiguration(String key) {
        Object object = this.get(key);
        if (object instanceof Map) {
            Configuration configuration = new Configuration();
            configuration.self = (Map<String, Object>) object;
            return configuration;
        }
        if (object instanceof Configuration)
            return (Configuration) object;
        return null;
    }

    public YamlConfigurable getYamlConfigurable(String key) {
        Configuration configuration = this.getConfiguration(key);
        if (configuration != null) {
            return new YamlConfigurable(configuration);
        }
        return null;
    }

    public Configuration asConfiguration() {
        return this.configuration;
    }

    @Override
    public void saveAsFile(Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
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
