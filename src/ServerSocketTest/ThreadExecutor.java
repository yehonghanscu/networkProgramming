package ServerSocketTest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author yehonghan
 * @2022/4/13 0:07
 */
public class ThreadExecutor {
    private ExecutorService executor;

    public ThreadExecutor(int maxPoolSize,int queueSize) {
        this.executor = new ThreadPoolExecutor(
                //线程池初始线程数
                maxPoolSize,
                //线程池最多线程数
                maxPoolSize,
                //任务队列存活时间
                120L,
                //线程延时时间
                TimeUnit.SECONDS,
                //任务队列存放Runnable任务
                new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task){
        this.executor.execute(task);
    }
}