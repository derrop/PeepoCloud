package net.peepocloud.lib.utility;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

public interface Callback<T> {
    void done(T t, Throwable throwable);
}
