import cache.LRUCache;
import org.junit.jupiter.api.Test;
import threadpool.DiscardPolicy;
import threadpool.RejectedEventHandler;
import threadpool.SimpleThreadPool;
import threadpool.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleThreadPoolTest {

    int initialSize = 1, coreSize = 2, maxSize = 4, queueSize = 5;
    long threadAliveTime = 2000, poolAliveTime = 5000;
    RejectedEventHandler handler = new DiscardPolicy();

    @Test
    void shouldExecuteTask(){

        SimpleThreadPool pool = new SimpleThreadPool(initialSize, coreSize, maxSize, queueSize, threadAliveTime, poolAliveTime, handler);

        int taskCount = 5;

        AtomicInteger atomicInteger = new AtomicInteger(0);
        CountDownLatch completed = new CountDownLatch(taskCount);
        LRUCache<String, Integer> cache = new LRUCache<>(16);


        for ( int i=1;i<=taskCount;i++ ){
            pool.execute(() ->{
                try {
                    new Task(cache, atomicInteger).run();
                }finally {
                    completed.countDown();
                }

            });

        }


        try {
            //等任务完成后，atomicInteger自增完毕，才能assertEquals
            assertTrue(completed.await(5, TimeUnit.SECONDS));
            assertEquals(taskCount, atomicInteger.get());
            pool.shutdown();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



    }

    @Test
    void shouldRejectTaskAfterShutdown(){
        SimpleThreadPool pool = new SimpleThreadPool(initialSize, coreSize, maxSize, queueSize, threadAliveTime, poolAliveTime, handler);

        pool.shutdown();
        assertThrows(IllegalThreadStateException.class, () -> pool.execute(() -> {
        }));


    }

}
