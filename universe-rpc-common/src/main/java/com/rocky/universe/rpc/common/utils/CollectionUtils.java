package com.rocky.universe.rpc.common.utils;

import java.util.Collection;

/**
 * Created by rocky on 17/12/14.
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}
