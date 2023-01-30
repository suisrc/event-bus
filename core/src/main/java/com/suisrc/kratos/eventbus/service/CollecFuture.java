package com.suisrc.kratos.eventbus.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import lombok.Getter;

/**
 * 
 * @author Y13
 *
 */
public class CollecFuture<T> implements Future<T> {

    @SuppressWarnings({"rawtypes"})
    public static final CollecFuture EMPTY_F = new CollecFuture<>(Collections.emptyList());
    
    @SuppressWarnings({"unchecked"})
    public static final <T> CollecFuture<T> empty() {
        return (CollecFuture<T>) EMPTY_F;
    }

    @Getter
    private final List<Future<T>> futures;
    private boolean cancelled = false;

    public CollecFuture( List<Future<T>> futures) {
        if (futures == null) {
            throw new NullPointerException();
        }
        this.futures = futures;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled = mayInterruptIfRunning;
        this.futures.forEach(t -> t.cancel(mayInterruptIfRunning));
        return false;
    }

    @Override
    public boolean isDone() {
        return this.futures.stream().anyMatch(Future::isDone);
    }

    /**
     * @result 最后一个结构
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        T r = null;
        for (Future<T> t : this.futures) {
            r = t.get();
        }
        return r;
    }

    /**
     * @result 最后一个结构
     */
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long exp = System.currentTimeMillis() + unit.toMillis(timeout);
        T r = null;
        for (Future<T> t : this.futures) {
            r = t.get(timeout, TimeUnit.MILLISECONDS);
            if ((timeout = exp - System.currentTimeMillis()) < 0) {
                throw new TimeoutException();
            }
        }
        return r;
    }
    
}
