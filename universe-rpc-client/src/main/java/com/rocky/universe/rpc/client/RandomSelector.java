package com.rocky.universe.rpc.client;

import java.util.Random;

/**
 * Created by rocky on 17/11/15.
 */
public class RandomSelector<T> implements Selector<T> {

    @Override
    public T select(T[] candidates) {
        int size = candidates.length;
        if (size == 0) {
            return null;
        }
        int r = intRandom(0, size - 1);
        return candidates[r];
    }


    /**
     * 生成一个int随机数
     *
     * @param min
     * @param max
     * @return
     */
    private static int intRandom(int min, int max) {
        if (min > max) throw new IllegalArgumentException("min can not bigger than max");
        if (max == min) return max;
        return new Random().nextInt(max - min + 1) + min;
    }
}
