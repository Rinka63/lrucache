package threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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


    private volatile boolean isShutdown = false;

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

        for(int i = 1; i <= initialSize; i++){
            Worker worker = new Worker(taskQueue, threads, threadAliveTime);
//            worker.setName("线程-" + threadCount);
            worker.start();
            threads.add(worker);
        }

        System.out.println("已初始化" + initialSize + "个线程");
        lastPoolActiveTime = System.currentTimeMillis();
    }


    //使用 sout 输出线程数和任务队列数，抛弃3个任务；不使用 sout 则抛弃4个任务
    @Override
    public void execute(Runnable task) {
        if(isShutdown){
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


    @Override
    public void shutdown() {
        isShutdown = true;
        while(isShutdown){
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

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        List<Runnable> remainingTasks = new ArrayList<>();
        taskQueue.drainTo(remainingTasks);

        for (Worker worker: threads){
            worker.interrupt();
        }
        return remainingTasks;
    }
}
