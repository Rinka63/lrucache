package cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于访问顺序的 LRU 缓存，超出容量时淘汰最久未访问的条目。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V>{
    private final int maxCapacity; //队列大小

    /**
     * 创建指定容量的缓存。
     *
     * @param capacity 最大缓存条目数
     */
    public LRUCache(int capacity){
        super(capacity, 0.75f, true);
        this.maxCapacity = capacity;
    }

    /**
     * 缓存超出容量时移除最久未访问的条目。
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest){
        if(size() > maxCapacity){
            System.out.println("已按照LRU策略移除最早的任务："+eldest.getKey());
        }
        return size() > maxCapacity;
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
