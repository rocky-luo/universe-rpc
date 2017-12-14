package com.rocky.universe.rpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by rocky on 17/12/13.
 */
public abstract class AbstractSelector<T> implements Selector<T>{
    @Override
    public T select(T[] candidates, T[] ignores) {
        if (ignores == null || ignores.length == 0) {
            return select(candidates);
        } else {
            List<T> candidateList = Lists.newArrayListWithCapacity(candidates.length);
            Set<T> ignoreSet = Sets.newHashSet(ignores);
            for (T candidate : candidates) {
                if (!ignoreSet.contains(candidate)) {
                    candidateList.add(candidate);
                }
            }
            return select((T[]) candidateList.toArray());
        }
    }
}
