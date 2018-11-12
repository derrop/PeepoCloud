package net.nevercloud.lib.conf;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface IConfigurable<V extends IConfigurable> {
    V append(String key, Object value);

    V append(String key, String value);

    V append(String key, Character value);

    V append(String key, Boolean value);

    V append(String key, Number value);

    boolean contains(String key);

    String getString(String key);

    boolean getBoolean(String key);

    char getCharacter(String key);

    byte getByte(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    BigInteger getBigInteger(String key);

    BigDecimal getBigDecimal(String key);

    <T> T getObject(String key, Class<T> tClass);

    Object getObject(String key, Type type);

    void saveAsFile(Path path);

    default void saveAsFile(String path) {
        this.saveAsFile(Paths.get(path));
    }

    byte[] toBytes();

}
