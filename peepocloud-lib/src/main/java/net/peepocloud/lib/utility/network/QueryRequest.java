package net.peepocloud.lib.utility.network;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class QueryRequest<T> {
    private CompletableFuture<T> future;

    public QueryRequest() {
        this.future = new CompletableFuture<>();
    }

    public void onComplete(Consumer<T> onComplete) {
        this.future.thenAccept(onComplete);
    }

    public T complete() {
        try {
            return this.future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public T complete(long timeout, TimeUnit timeUnit) {
        try {
            return this.future.get(timeout, timeUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.err.println("Could not get a response for the query in the given time");
            e.printStackTrace();
            return null;
        }
    }

    public void setResponse(T response) {
        this.future.complete(response);
    }
}
