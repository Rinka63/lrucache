package cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V>{
    private final int MAX_CAPACITY; //队列大小

    public LRUCache(int capacity){
        super(capacity, 0.75f, true);
        this.MAX_CAPACITY = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest){
        if(size() > MAX_CAPACITY){
            System.out.println("已移除最早的任务："+eldest.getKey());
        }
        return size() > MAX_CAPACITY;
    }

    @Override
    public synchronized V put(K key, V value){
        return super.put(key, value);
    }

    @Override
    public synchronized V get(Object key){
        return super.get(key);
    }

    @Override
    public synchronized V remove(Object key){
        return super.remove(key);
    }

    @Override
    public synchronized void clear(){
        super.clear();
    }

    @Override
    public synchronized int size() {
        return super.size();
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public synchronized void putAll(
            Map<? extends K, ? extends V> map) {
        super.putAll(map);
    }

}

