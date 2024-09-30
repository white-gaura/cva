package com.whitecrow.blog.singleton;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WhiteCrow
 * 这东西感觉真不适合点赞这一类，反而有点适合播放量这种没有取消操作
 */

public class BlogLikeSingleton {
    private static volatile BlogLikeSingleton instance;
    private final ConcurrentHashMap<Long, Set<Long>> hashMap;
    final Object lock = new Object();


    private BlogLikeSingleton() {
        // 初始化哈希表
        hashMap = new ConcurrentHashMap<>();
    }

    public static BlogLikeSingleton getInstance() {
        if (instance == null) {
            synchronized (BlogLikeSingleton.class) {
                if (instance == null) {
                    instance = new BlogLikeSingleton();
                }
            }
        }
        return instance;
    }

    public void put(Long key, Set<Long> value) {
        hashMap.put(key, value);
    }

    public Set<Long> get(Long key) {
        return hashMap.get(key);
    }

    public Set<Long> getKeySet() {
        return hashMap.keySet();
    }


    public void addToSet(Long key, Long data) {
        //如果key不存在，创建一个新set
        synchronized (lock) {
            hashMap.putIfAbsent(key, new HashSet<>());
            //获取对应的set并添加数据
            Set<Long> dataSet = BlogLikeSingleton.instance.get(key);
            dataSet.add(data);
        }
    }

    public boolean findToSet(Long key, Long data) {
        if (hashMap.get(key) == null) {
            return false;
        }
        return hashMap.get(key).contains(data);
    }

    public void deleteSet(Long key, Long data) {
        synchronized (lock) {
            //获取对应的set并添加数据
            Set<Long> dataSet = BlogLikeSingleton.instance.get(key);
            dataSet.remove(data);
        }
    }
    public Long countToSet(Long key){
        return (long) hashMap.get(key).size();
    }
    public void clear() {
        hashMap.clear();
    }
}
