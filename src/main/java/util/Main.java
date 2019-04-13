package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private final static Logger logger= LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){
        logger.info("测试一下");
        logger.error("error测试");
        logger.debug("debug测试");
    }
}
