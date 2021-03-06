package net.peepocloud.lib.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Template implements Serializable {
    private String name;
    private String storage;

    /**
     * Gets the name of this {@link Template}
     *
     * @return the name of this {@link Template}
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the storage of this {@link Template}
     *
     * @return the storage of this {@link Template}
     */
    public String getStorage() {
        return storage;
    }
}
