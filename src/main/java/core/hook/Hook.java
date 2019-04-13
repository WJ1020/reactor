package core.hook;

import core.process.Status;

public class Hook extends Thread {

    @Override
    public void run() {
        Status.running=false;
    }
}
