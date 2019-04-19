package core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程
 */
public class ReactorThread extends Thread {
    /**
     * 默认线程名称
     */
    private final static String DEFAULT_NAME="reactor_thread";
    /**
     * 是否打印日志
     */
    private static volatile boolean debugLifeCycle=true;
    /**
     * 当前第几个线程创建
     */
    private static final AtomicInteger created=new AtomicInteger();
    /**
     * 当前活跃的线程个数
     */
    private static final AtomicInteger alive=new AtomicInteger();

    private final Logger logger= LoggerFactory.getLogger(ReactorThread.class);


    public ReactorThread(Runnable runnable){
        this(runnable,DEFAULT_NAME);
    }

    public ReactorThread(Runnable runnable,String threadName){
        super(runnable,threadName+"-"+created.incrementAndGet());
        setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler(){
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.error("UNCAUGHT int thread :{} {}:",t.getName(),e.getMessage());
                    }
                }
        );

    }

    @Override
    public void run() {
        boolean debug=debugLifeCycle;
        if (debug){
            logger.info("start thread : {}",getName());
        }
        try {
            alive.incrementAndGet();
            super.run();
        }finally {
            alive.decrementAndGet();
            if (debug){
                logger.info("exiting thread： {}",getName());
            }
        }
    }
    public static int getCreated(){
        return created.get();
    }

    public static int getAlive() {
        return alive.get();
    }

    public static void setDebug(boolean debugLifeCycle) {
        ReactorThread.debugLifeCycle = debugLifeCycle;
    }
}
