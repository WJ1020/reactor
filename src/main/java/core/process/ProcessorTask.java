package core.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ProcessorTask implements Runnable {

    private final static Logger logger= LoggerFactory.getLogger(ProcessorTask.class);
    private Selector selector;

    ProcessorTask(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        logger.info("{}\tsub reactor start listener",Thread.currentThread().getName());
        while (Status.running){
            try {
                selector.select();
                Set<SelectionKey> keys=selector.selectedKeys();
                Iterator<SelectionKey> iterator=keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key=iterator.next();
                    iterator.remove();
                    if (key.isReadable()){
                        ByteBuffer buffer= ByteBuffer.allocate(1024);
                        SocketChannel socketChannel= (SocketChannel) key.channel();
                        int count=socketChannel.read(buffer);
                        if (count<0){
                            socketChannel.close();
                            key.cancel();
                            logger.info("{}\t Read ended",socketChannel);
                        }else if (count==0){
                            logger.info("{}\t Message size is 0",socketChannel);
                        }else {
                            buffer.flip();
                            socketChannel.write(buffer);
                            logger.info("{}\t Read message{}",socketChannel,new String(buffer.array()));
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("select error :{}",e.getMessage());
            }

        }
    }
}
