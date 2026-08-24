import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import java.util.concurrent.atomic.AtomicInteger;
import cache.LRUCache;

import threadpool.*;

public class Main {
    //TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。


    //todo：为什么执行后任务队列数为1,

    public static void main(String[] args) throws InterruptedException {


        int threadNum = 20, capacity = 16;
        LRUCache<String, Integer> cache = new LRUCache<>(capacity);

        int initialSize = 1, coreSize = 2, maxSize = 4, queueSize = 5;
        long threadAliveTime = 2000, poolAliveTime = 5000;
        RejectedEventHandler handler = new DiscardPolicy();

        SimpleThreadPool pool = new SimpleThreadPool(initialSize, coreSize, maxSize, queueSize, threadAliveTime, poolAliveTime, handler);



        System.out.printf("该项目使用 线程池 和 LRU 练习 Java的并发机制。%n Task到来时，线程池分配线程把 task 放入 LRU缓存，缓存放满后删除最早的数据；%n ");
        System.out.printf("线程池线程数 < coreSize 时创建线程；%n 线程数 > coreSize 且 线程数 < maxSize 时放入队列；%n 队列放满 且 线程数 <= maxSize 时创建线程；%n 线程数 > maxSize 后丢弃任务%n");
        System.out.printf(" 线程活跃间隔 > threadAliveTime 且 线程数 > 1 时，线程失活；%n 若 线程数 == 1 且 线程池活跃间隔 > poolAliveTime，关闭线程池%n");
        System.out.println();

        AtomicInteger atomicInteger = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<threadNum; i++){

            pool.execute(new Task(cache, atomicInteger));
        }

        System.out.println("Cache size:" + cache.size());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(String.format("Time cost：%dms", duration));
        pool.shutdown();



//        线程池测试代码
//        int initialSize = 1, coreSize = 2, maxSize = 4, queueSize = 3;
//        long threadAliveTime = 1000, poolAliveTime = 4000;
//        RejectedEventHandler handler = new DiscardPolicy();
//
//        SimpleThreadPool pool = new SimpleThreadPool(initialSize, coreSize, maxSize, queueSize, threadAliveTime, poolAliveTime, handler);
//        Random random = new Random();
//        for (int i = 1; i <= 10; i++) {
//            Task task = new Task(i);
////            int r = random.nextInt(1001) + 500;
////            Thread.sleep(r);
//            pool.execute(task);
//        }
//        Thread.sleep(1000);
//
//        todo：看arrayList源码。下方循环不使用sleep()，线程池会创建新线程。
//        for (int i = 11; i <= 30; i++) {
//            Task task = new Task(i);
//            int r = random.nextInt(1001) + 500;
//            Thread.sleep(r);
//            pool.execute(task);
//        }




//        LRUCache<String, Integer> cache = new LRUCache<>(4);
//        cache.put("AA", 1);
//        cache.put("BB", 2);
//        cache.put("CC", 3);
//        cache.put("DD", 4);
//
//        cache.get("AA");
//
//        for (Object key: cache.keySet()){
//            System.out.println(key.toString());
//        }
//
//        cache.put("EE", 5);
//        System.out.println();
//
//        for (Object key: cache.keySet()){
//            System.out.println(key.toString());
//        }










    }
}
