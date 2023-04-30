package project.follower;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import project.Config;

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
        while(!this.parentMessageHandler.getSocket().isClosed()){
            if(this.parentMessageHandler.getLastHeartbeat() != null){
                long delta = this.parentMessageHandler.getLastHeartbeat().until(Instant.now(), ChronoUnit.MILLIS);
                if(delta >= Config.HEARTBEAT_TIMEOUT){
                    this.parentMessageHandler.leaderTimedOut();
                    System.out.println(this.parentMessageHandler.getParentNode().getIp() + " ran into heartbeat timeout for leader");
                }
            }
        }
    }
}
