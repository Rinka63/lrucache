package threadpool;

import java.util.List;

public interface ThreadPool {

    void execute(Runnable task);

    void shutdown();

    List<Runnable> shutdownNow();

}
