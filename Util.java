import java.util.concurrent.TimeUnit;

public abstract class Util {
    
    public static void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
    }
}
