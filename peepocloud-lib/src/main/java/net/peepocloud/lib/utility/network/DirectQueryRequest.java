package net.peepocloud.lib.utility.network;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@AllArgsConstructor
public class DirectQueryRequest<T> extends QueryRequest<T> {

    @Getter
    private T value;

    @Override
    public void onComplete(Consumer<T> onComplete) {
        onComplete.accept(value);
    }

    @Override
    public T complete() {
        return value;
    }

    @Override
    public T complete(long timeout, TimeUnit timeUnit) {
        return value;
    }

    @Override
    public void setResponse(T response) {
        throw new UnsupportedOperationException("not supported in DirectQueryRequest");
    }
}
