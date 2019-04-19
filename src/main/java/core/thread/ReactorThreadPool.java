package core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ReactorThreadPool extends ThreadPoolExecutor {

    private final Logger logger= LoggerFactory.getLogger(ThreadPoolExecutor.class);

    private final ThreadLocal<Long> startTime=new ThreadLocal<>();
    private final AtomicLong numTasks=new AtomicLong();
    private final AtomicLong totalTime=new AtomicLong();

    public ReactorThreadPool(){
        this(Runtime.getRuntime().availableProcessors(),Runtime.getRuntime().availableProcessors(),0L,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(100)
        ,new ReactorThreadFactory("reactor"),new CallerRunsPolicy());
    }

    public ReactorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        logger.debug("thread {} start {}",t.getName(),r.getClass());
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        long endTime=System.nanoTime();
        long taskTime=endTime-startTime.get();
        numTasks.incrementAndGet();
        totalTime.addAndGet(taskTime);
        logger.info(" Thread %s :end %s, time=%dns",t,r,taskTime);
    }

    @Override
    protected void terminated() {
        super.terminated();
        try {
            System.out.println(String.format("Terminated : avg time=%dns",totalTime.get()/numTasks.get()));
            logger.debug("Terminated : avg time={}",totalTime.get()/numTasks.get());
        }finally {
            super.terminated();
        }
    }
}
