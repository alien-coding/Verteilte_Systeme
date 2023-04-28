package Project;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import Project.follower.FollowerLeaderMessageHandler;

public class CheckHeartbeat extends Thread {
    private FollowerLeaderMessageHandler parentMessageHandler;
    
    public CheckHeartbeat(FollowerLeaderMessageHandler parentMessageHandler){
        this.parentMessageHandler = parentMessageHandler;
    }
    
    /**
     * Checks the difference between now and the last leader heartbeat. If it goes above Config.HEARTBEAT_TIMEOUT,
     * the FollowerLeaderMessageHandler.leaderTimedOut() is called and own thread is stopped.
     */
    public void run(){
        Boolean timedOut = false;
        while(!timedOut){
            if(this.parentMessageHandler.getLastHeartbeat() != null){
                long delta = this.parentMessageHandler.getLastHeartbeat().until(Instant.now(), ChronoUnit.MILLIS);
                if(delta >= Config.HEARTBEAT_TIMEOUT){
                    timedOut = true;
                    System.out.println(this.parentMessageHandler.getParentNode().getIp() + " ran into heartbeat timeout for leader.");
                    this.parentMessageHandler.leaderTimedOut();
                }
            }
        }
    }
}
