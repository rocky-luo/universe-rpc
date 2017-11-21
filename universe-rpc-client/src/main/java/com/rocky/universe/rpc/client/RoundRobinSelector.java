package com.rocky.universe.rpc.client;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rocky on 17/11/14.
 */
public class RoundRobinSelector<T> implements Selector<T> {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public T select(T[] candidates) {
        if (candidates.length == 0) {
            return null;
        }
        int selectNum = count.incrementAndGet() % candidates.length;
        return candidates[selectNum];

    }
}
