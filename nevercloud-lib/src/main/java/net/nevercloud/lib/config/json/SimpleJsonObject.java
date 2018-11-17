package net.nevercloud.lib.config.json;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.common.base.Preconditions;
import com.google.gson.*;
import net.nevercloud.lib.config.Configurable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SimpleJsonObject implements Configurable<SimpleJsonObject> {

    public static final JsonParser PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonObject jsonObject;

    public SimpleJsonObject() {
        this(new JsonObject());
    }

    public SimpleJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public SimpleJsonObject(String input) {
        JsonElement jsonElement = PARSER.parse(input);
        Preconditions.checkArgument(jsonElement.isJsonObject(), "JsonInput must be an json object, not " + jsonElement.getClass().getSimpleName());
        this.jsonObject = jsonElement.getAsJsonObject();
    }

    public SimpleJsonObject(Reader reader) {
        JsonElement jsonElement = PARSER.parse(reader);
        Preconditions.checkArgument(jsonElement.isJsonObject(), "JsonInput must be an json object, not " + jsonElement.getClass().getSimpleName());
        this.jsonObject = jsonElement.getAsJsonObject();
    }

    public SimpleJsonObject append(String key, Object value) {
        if (value == null) {
            this.jsonObject.add(key, JsonNull.INSTANCE);
            return this;
        }

        this.jsonObject.add(key, GSON.toJsonTree(value));
        return this;
    }

    public SimpleJsonObject append(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public SimpleJsonObject append(String key, Character value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public SimpleJsonObject append(String key, Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public SimpleJsonObject append(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public boolean contains(String key) {
        return this.jsonObject.has(key) && !(this.jsonObject.get(key) instanceof JsonNull);
    }

    public JsonElement get(String key) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (jsonElement instanceof JsonNull)
            return null;
        return jsonElement;
    }

    public String getString(String key) {
        return contains(key) ? get(key).getAsString() : null;
    }

    public boolean getBoolean(String key) {
        return contains(key) && get(key).getAsBoolean();
    }

    public char getCharacter(String key) {
        return contains(key) ? get(key).getAsCharacter() : (char) 0;
    }

    public byte getByte(String key) {
        return contains(key) ? get(key).getAsByte() : -1;
    }

    public short getShort(String key) {
        return contains(key) ? get(key).getAsShort() : -1;
    }

    public int getInt(String key) {
        return contains(key) ? get(key).getAsInt() : -1;
    }

    public long getLong(String key) {
        return contains(key) ? get(key).getAsLong() : -1;
    }

    public BigInteger getBigInteger(String key) {
        return contains(key) ? get(key).getAsBigInteger() : null;
    }

    public BigDecimal getBigDecimal(String key) {
        return contains(key) ? get(key).getAsBigDecimal() : null;
    }

    public <T> T getObject(String key, Class<T> tClass) {
        return GSON.fromJson(this.get(key), tClass);
    }

    public Object getObject(String key, Type type) {
        return GSON.fromJson(this.get(key), type);
    }

    public JsonObject asJsonObject() {
        return jsonObject;
    }

    public void saveAsFile(Path path) {
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                    Files.createFile(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path, StandardOpenOption.CREATE), StandardCharsets.UTF_8)) {
            GSON.toJson(this.jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SimpleJsonObject load(String path) {
        return load(Paths.get(path));
    }

    public static SimpleJsonObject load(Path path) {
        if (!Files.exists(path))
            return new SimpleJsonObject();
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            return new SimpleJsonObject(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SimpleJsonObject();
    }

    public String toJson() {
        return this.jsonObject.toString();
    }

    public String toPrettyJson() {
        return GSON.toJson(this.jsonObject);
    }

    public byte[] toBytes() {
        return toJson().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toPrettyBytes() {
        return toPrettyJson().getBytes(StandardCharsets.UTF_8);
    }
}
