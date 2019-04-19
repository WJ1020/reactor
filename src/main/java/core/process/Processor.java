package core.process;

import core.thread.ReactorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ExecutorService;

public class Processor {
    private static final Logger logger= LoggerFactory.getLogger(Processor.class);
    private static final ExecutorService service=
            new ReactorThreadPool();
    private final Selector selector;

    private volatile boolean running=true;

    public Processor() throws IOException {
        this.selector= SelectorProvider.provider().openSelector();
    }

    public void addChannel(SocketChannel socketChannel){
        try {
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            if (running){
                running=false;
                start();
            }
            wakeup();
        } catch (ClosedChannelException e) {
            logger.error("register channel error :{}",e.getMessage());
        }
    }

    private void wakeup(){
        this.selector.wakeup();
    }

    private void start(){
        service.submit(new ProcessorTask(selector));
    }
}
