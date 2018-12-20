package net.peepocloud.lib.config;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnmodifiableConfigurable<T extends Configurable> implements Configurable<T> {
    private Configurable<T> configurable;

    @Override
    public T append(String key, Object value) {
        throw new UnsupportedOperationException("UnmodifableConfigurable cannot be edited");
    }

    @Override
    public T append(String key, String value) {
        throw new UnsupportedOperationException("UnmodifableConfigurable cannot be edited");
    }

    @Override
    public T append(String key, Character value) {
        throw new UnsupportedOperationException("UnmodifableConfigurable cannot be edited");
    }

    @Override
    public T append(String key, Boolean value) {
        throw new UnsupportedOperationException("UnmodifableConfigurable cannot be edited");
    }

    @Override
    public T append(String key, Number value) {
        throw new UnsupportedOperationException("UnmodifableConfigurable cannot be edited");
    }

    @Override
    public boolean contains(String key) {
        return this.configurable.contains(key);
    }

    @Override
    public String getString(String key) {
        return this.configurable.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return this.configurable.getBoolean(key);
    }

    @Override
    public char getCharacter(String key) {
        return this.configurable.getCharacter(key);
    }

    @Override
    public byte getByte(String key) {
        return this.configurable.getByte(key);
    }

    @Override
    public short getShort(String key) {
        return this.configurable.getShort(key);
    }

    @Override
    public int getInt(String key) {
        return this.configurable.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return this.configurable.getLong(key);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return this.configurable.getBigInteger(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return this.configurable.getBigDecimal(key);
    }

    @Override
    public <T1> T1 getObject(String key, Class<T1> t1Class) {
        return this.configurable.getObject(key, t1Class);
    }

    @Override
    public Object getObject(String key, Type type) {
        return this.configurable.getObject(key, type);
    }

    @Override
    public void saveAsFile(Path path) {
        this.configurable.saveAsFile(path);
    }

    @Override
    public byte[] toBytes() {
        return this.configurable.toBytes();
    }

    public static <T extends Configurable> UnmodifiableConfigurable<T> create(Configurable<T> configurable) {
        return new UnmodifiableConfigurable<>(configurable);
    }
}
