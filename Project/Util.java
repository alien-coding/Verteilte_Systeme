package project;
import java.util.concurrent.TimeUnit;

public abstract class Util {
    
    /**
     * lets threads sleep for specified amount of milliseconds 
     * @param milliseconds
     */
    public static void sleep(long milliseconds){
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
    }
}
