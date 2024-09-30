package com.whitecrow.chat.singleton;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WhiteCrow
 *
 */

public class NettySingleton {
    private static volatile NettySingleton instance;
    private final ConcurrentHashMap<Object, Object> hashMap;


    private NettySingleton() {
        // 初始化哈希表
        hashMap = new ConcurrentHashMap<>();
    }

    public static NettySingleton getInstance() {
        if (instance == null) {
            synchronized (NettySingleton.class) {
                if (instance == null) {
                    instance = new NettySingleton();
                }
            }
        }
        return instance;
    }

    public void put(Object key, Object value) {
        hashMap.put(key, value);
    }

    public Object get(Object key) {
        return hashMap.get(key);
    }
    public Set<Object> getKeySet(){
        return hashMap.keySet();
    }
    public void clear() {
        hashMap.clear();
    }
}
