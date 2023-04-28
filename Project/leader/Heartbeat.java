package Project.leader;

import Project.Config;
import Project.Util;
import Project.message.Message;
import Project.message.MessageType;

public class Heartbeat extends Thread {
    private LeaderMessageHandler parentMessageHandler;
    
    /**
     * 
     * @param leader Leader who is initializing Heartbeat
     * @param HEARTBEAT_INTERVAL in s
     */
    public Heartbeat(LeaderMessageHandler parentMessageHandler){
        this.parentMessageHandler = parentMessageHandler;
    }

    public void run(){
        while(!this.parentMessageHandler.getSocket().isClosed()){
            String sender = this.parentMessageHandler.getParentLeader().getParentNode().getIp();
            String receiver = this.parentMessageHandler.getFollowerIp();
            Message heartbeat = new Message(sender, receiver, "heartbeat", MessageType.HEARTBEAT);
            this.parentMessageHandler.sendMessage(heartbeat);
            Util.sleep(Config.HEARTBEAT_INTERVAL);
        }
    }
}
