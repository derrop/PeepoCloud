package net.peepocloud.lib.utility;
/*
 * Created by Mc_Ruben on 19.01.2019
 */

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {

    private Map<K, V> map;

    public MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public MapBuilder() {
        this(new HashMap<>());
    }

    public MapBuilder<K, V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> get() {
        return this.map;
    }

}
