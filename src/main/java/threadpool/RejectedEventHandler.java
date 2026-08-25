package threadpool;

public interface RejectedEventHandler {
    void rejectedEvent(Runnable r, ThreadPool pool);
}
