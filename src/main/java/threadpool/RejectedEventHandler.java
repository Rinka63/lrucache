package threadpool;

/**
 * 线程池任务拒绝处理器。
 */
public interface RejectedEventHandler {

    /**
     * 处理线程池无法接收的任务。
     *
     * @param r 被拒绝的任务
     * @param pool 触发拒绝的线程池
     */
    void rejectedEvent(Runnable r, ThreadPool pool);
}
