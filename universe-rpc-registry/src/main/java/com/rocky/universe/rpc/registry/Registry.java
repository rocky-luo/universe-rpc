package com.rocky.universe.rpc.registry;

/**
 * Created by rocky on 17/10/13.
 */
public interface Registry {
    void register();
    void unregister();

    void subscribe(String url, NotifyListener listener);
    void unSubscribe(String url, NotifyListener listener);
}
