package threadpool;

import java.util.concurrent.atomic.AtomicInteger;
import cache.LRUCache;

public class Task implements Runnable{

    private final AtomicInteger taskId;

    private final LRUCache<String, Integer> cache;

    public Task(LRUCache<String, Integer> cache, AtomicInteger taskId){
        this.cache = cache;
        this.taskId = taskId;

    }

    @Override
    public void run() {
//        System.out.println("正在运行任务" + this.taskId);
        int value = taskId.incrementAndGet();
        cache.put("id" + value, value);

        System.out.printf("线程%d 完成任务 %d%n", Thread.currentThread().getId(), value);

//        cache.remove("id" + value);
    }
}
