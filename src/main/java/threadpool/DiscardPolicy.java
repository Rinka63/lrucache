package threadpool;

public class DiscardPolicy implements  RejectedEventHandler{
    @Override
    public void rejectedEvent(Runnable r, ThreadPool pool) {
        System.out.println("任务队列已满，抛弃一个任务");
    }
}
