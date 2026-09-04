package threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 简单线程池，支持任务排队、扩容及优雅关闭。
 */
public class SimpleThreadPool implements  ThreadPool{

    private final int initialSize;

    private final int coreSize;

    private final int maxSize;

    private final int queueSize;

    private final long threadAliveTime;

    private BlockingQueue<Runnable> taskQueue;

    private List<Worker> threads;

    private final RejectedEventHandler eventHandler;

    private final long poolAliveTime;

    private volatile long lastPoolActiveTime;


    private volatile boolean shutDownFlag = false;

    /**
     * 创建线程池并启动初始工作线程。
     *
     * @param initialSize 初始线程数
     * @param coreSize 核心线程数
     * @param maxSize 最大线程数
     * @param queueSize 任务队列容量
     * @param threadAliveTime 工作线程空闲存活时间
     * @param poolAliveTime 线程池关闭前的空闲等待时间
     * @param handler 任务拒绝处理器
     */
    public SimpleThreadPool(int initialSize, int coreSize, int maxSize, int queueSize, long threadAliveTime, long poolAliveTime, RejectedEventHandler handler){
        this.initialSize = initialSize;
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.queueSize = queueSize;
        this.threadAliveTime = threadAliveTime;
        this.poolAliveTime = poolAliveTime;
        taskQueue = new LinkedBlockingQueue<>(this.queueSize);
        threads = new ArrayList<>();
        eventHandler = handler;

        initWorkers();


    }

    /**
     * 初始化线程
     *
     * @param
     */
    private void initWorkers(){

        for( int i=1;i<=this.initialSize;i++ ){
            Worker worker = new Worker(taskQueue, threads, threadAliveTime);
            worker.start();
            threads.add(worker);
        }
        System.out.println("已初始化" + initialSize + "个线程");
        lastPoolActiveTime = System.currentTimeMillis();
    }



    /**
     * 提交任务；队列和线程均达到上限时交由拒绝处理器处理。
     *
     * @param task 待执行任务
     * @throws IllegalThreadStateException 线程池已关闭时抛出
     */
    @Override
    public void execute(Runnable task) {
        if(shutDownFlag){
            throw new IllegalThreadStateException("线程池处于关闭状态");
        }

        if(threads.size() < coreSize){
            addWorkerThread(task);
            System.out.printf("创建新线程。线程数：%d，任务队列数：%d%n", threads.size(), taskQueue.size());
        }else if(!taskQueue.offer(task)){
            if(threads.size() < maxSize){
                addWorkerThread(task);
                System.out.printf("任务队列已满，创建新线程。线程数：%d，任务队列数：%d%n", threads.size(), taskQueue.size());
            }else if (threads.size() >= maxSize){
                eventHandler.rejectedEvent(task, this);
            }
        }
        lastPoolActiveTime = System.currentTimeMillis();



    }



    private void addWorkerThread(Runnable task){
        Worker worker = new Worker(taskQueue, threads, this.threadAliveTime);
        worker.start();

        threads.add(worker);
        taskQueue.offer(task);
        lastPoolActiveTime = System.currentTimeMillis();
    }

    /**
     * 等待任务完成后关闭线程池。
     */
    @Override
    public void shutdown() {
        shutDownFlag = true;
        while(shutDownFlag){
            if(taskQueue.isEmpty() && (System.currentTimeMillis() - lastPoolActiveTime > poolAliveTime)){

                for(Worker worker: threads){
                    worker.interrupt();
                }
                System.out.printf("线程数：%d，任务队列数：%d，线程池已关闭%n", threads.size(), taskQueue.size());
                return;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        for(Worker worker: threads){
//            worker.interrupt();
//        }

    }
    /**
     * 立即关闭线程池，并返回尚未执行的任务。
     *
     * @return 未执行的任务列表
     */
    @Override
    public List<Runnable> shutdownNow() {
        shutDownFlag = true;
        List<Runnable> remainingTasks = new ArrayList<>();
        taskQueue.drainTo(remainingTasks);

        for (Worker worker: threads){
            worker.interrupt();
        }
        return remainingTasks;
    }
}
