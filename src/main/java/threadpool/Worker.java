package threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker extends Thread {

    private BlockingQueue<Runnable> taskQueue;

    private List<Worker> threads;

    private long threadAliveTime;

    private static final AtomicInteger workerCount = new AtomicInteger(0);


    public Worker(BlockingQueue<Runnable> queue, List<Worker> threads, long threadAliveTime){
        this.taskQueue = queue;
        this.threads = threads;
        this.threadAliveTime = threadAliveTime;
        workerCount.incrementAndGet();
    }



    //todo:只有一个线程的时候不remove(this);
    @Override
    public void run() {
        long lastActiveTime = System.currentTimeMillis();


        while(!Thread.currentThread().isInterrupted() ){
            try {
                Runnable task = taskQueue.poll(threadAliveTime, TimeUnit.MILLISECONDS);
                if(task != null){
                    task.run();

                    lastActiveTime = System.currentTimeMillis();
                }else if(System.currentTimeMillis() - lastActiveTime > this.threadAliveTime){
                    synchronized (threads){
                        if(threads.size() > 1){

                            System.out.printf("线程 %d 已失活%n", Thread.currentThread().getId());
                            threads.remove(this);
                            workerCount.decrementAndGet();
                            break;
                        }
                    }




                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }

    }

    public static int getWorkerCount(){
        return workerCount.get();
    }

}
