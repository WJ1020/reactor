package core.thread;

import java.util.concurrent.ThreadFactory;

public class ReactorThreadFactory implements ThreadFactory {

    private final String threadName;

    public ReactorThreadFactory(String threadName) {
        this.threadName = threadName;
    }
    @Override
    public Thread newThread(Runnable r) {
        return new ReactorThread(r,threadName);
    }
}
