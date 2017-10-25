package com.rokcy.universe.rpc.server;

/**
 * Created by rocky on 17/10/13.
 */
public interface Registry {
    void register(String url, String data);
    void unregister(String url);
}
