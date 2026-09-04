import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import java.util.concurrent.atomic.AtomicInteger;
import cache.LRUCache;

import threadpool.*;

public class Main {


    /**
     * Java 并发机制练习。
     *
     * <p>项目使用线程池和 LRU 缓存：
     * Task 到来时，由线程池分配线程执行任务，并将 Task 放入 LRU 缓存；
     * 当缓存容量达到上限时，淘汰最早使用的数据。
     *
     * <p>线程池任务处理规则：
     * <ul>
     *     <li>线程数小于 coreSize 时，创建新线程。</li>
     *     <li>达到 coreSize 后，任务优先进入工作队列。</li>
     *     <li>工作队列已满且线程数小于 maxSize 时，创建新线程。</li>
     *     <li>达到最大线程数后，根据拒绝策略处理任务。</li>
     * </ul>
     *
     * <p>线程回收规则：
     * <ul>
     *     <li>线程空闲时间超过 threadAliveTime 时，允许回收非核心空闲线程。</li>
     *     <li>线程池长时间无任务时，根据项目定义的 poolAliveTime 执行关闭逻辑。</li>
     * </ul>
     *
     *<p>
     *     已上传到 github，设置为private项目
     *     参考《阿里巴巴Java开发手册》重构变量名和方法名
     *</p>
     *
     */
    public static void main(String[] args) {


        int taskNum = 20, capacity = 16;
        LRUCache<String, Integer> cache = new LRUCache<>(capacity);

        int initialSize = 1, coreSize = 2, maxSize = 4, queueSize = 5;
        long threadAliveTime = 2000, poolAliveTime = 5000;
        RejectedEventHandler handler = new DiscardPolicy();

        SimpleThreadPool pool = new SimpleThreadPool(initialSize, coreSize, maxSize, queueSize, threadAliveTime, poolAliveTime, handler);


        AtomicInteger atomicInteger = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        for( int i=0; i<taskNum; i++ ){

            pool.execute(new Task(cache, atomicInteger));
        }

        System.out.println("Cache size:" + cache.size());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(String.format("Time cost：%dms", duration));


        pool.shutdown();










    }
}
