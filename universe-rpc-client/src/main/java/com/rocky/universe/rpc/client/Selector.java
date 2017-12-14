package com.rocky.universe.rpc.client;

import java.util.List;

/**
 * Created by rocky on 17/11/14.
 */
public interface Selector<T> {
    T select(T[] candidates);
    T select(T[] candidates, T[] ignores);
}
