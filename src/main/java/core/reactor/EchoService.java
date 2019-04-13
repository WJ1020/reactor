package core.reactor;

import core.process.Processor;
import core.process.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoService {

    private static final Logger logger= LoggerFactory.getLogger(EchoService.class);

    private final String ip;
    private final int port;
    public EchoService(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start(){
        logger.info("echo service start......");
        try {
            Selector selector=Selector.open();
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(ip,port))
                    .configureBlocking(false)
                    .register(selector,SelectionKey.OP_ACCEPT);

            int coreNum = Runtime.getRuntime().availableProcessors();
            Processor[] processors = new Processor[coreNum];
            for (int i = 0; i < processors.length; i++) {
                logger.info("creat processor :{}",i+1);
                processors[i] = new Processor();
            }
            int index=0;
            while (Status.running){
                selector.select();
                Set<SelectionKey> keys=selector.selectedKeys();
                Iterator<SelectionKey> iterator=keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey=iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()){
                        ServerSocketChannel currServerSocketChannel= (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel=currServerSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        logger.info("Accept request from {}",socketChannel.getRemoteAddress());
                        Processor processor=processors[(++index)%coreNum];
                        processor.addChannel(socketChannel);

                    }
                }
            }
        } catch (IOException e) {
            logger.error("io exception {}",e.getMessage());
        }
    }

}
