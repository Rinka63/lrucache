package threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工作线程，负责获取并执行任务。
 */
public class Worker extends Thread {

    /** 待执行任务队列。 */
    private BlockingQueue<Runnable> taskQueue;

    /** 工作线程列表，用于空闲线程回收。 */
    private List<Worker> threads;

    /** 非核心线程的空闲存活时间（毫秒）。 */
    private long threadAliveTime;

    /** 当前工作线程总数。 */
    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(0);


    /**
     * 创建一个工作线程。
     *
     * @param queue 任务队列
     * @param threads 工作线程列表
     * @param threadAliveTime 空闲存活时间（毫秒）
     */
    public Worker(BlockingQueue<Runnable> queue, List<Worker> threads, long threadAliveTime){
        this.taskQueue = queue;
        this.threads = threads;
        this.threadAliveTime = threadAliveTime;
        WORKER_COUNT.incrementAndGet();
    }




    /** 获取并执行任务，空闲超时后回收非核心线程。 */
    @Override
    public void run() {
        long lastActiveTime = System.currentTimeMillis();


        while( !Thread.currentThread().isInterrupted()){
            Runnable task;

            try {
                task = taskQueue.poll(threadAliveTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if(task != null){
                try {
                    task.run();
                } catch (RuntimeException e) {
                    // 单个任务失败不应导致工作线程退出。
                    e.printStackTrace();
                }

                lastActiveTime = System.currentTimeMillis();
            }else if(System.currentTimeMillis() - lastActiveTime > this.threadAliveTime){
                synchronized (threads){
                    if(threads.size() > 1){

                        System.out.printf("线程 %d 已失活%n", Thread.currentThread().getId());
                        threads.remove(this);
                        WORKER_COUNT.decrementAndGet();
                        break;
                    }
                }
            }

        }

    }

    /**
     * 返回当前工作线程总数。
     *
     * @return 工作线程数量
     */
    public static int getWorkerCount(){
        return WORKER_COUNT.get();
    }

}
