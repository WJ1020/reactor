import core.hook.Hook;
import core.reactor.EchoService;

public class EchoServiceStart {

    public static void main(String[] args){
        EchoService echoService=new EchoService("127.0.0.1",80);
        echoService.start();
        Runtime.getRuntime().addShutdownHook(new Hook());
    }
}
