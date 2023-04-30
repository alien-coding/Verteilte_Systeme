package project;
import java.util.concurrent.TimeUnit;

public abstract class Util {
    
    public static void sleep(long milliseconds){
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
    }
}
