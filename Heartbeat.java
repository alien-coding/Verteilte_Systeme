import Message.Message;
import Message.MessageType;

public class Heartbeat extends Thread {
    private LeaderMessageHandler parentMessageHandler;

    /**
     * 
     * @param leader Leader who is initializing Heartbeat
     * @param heartbeatInterval in s
     */
    public Heartbeat(LeaderMessageHandler parentMessageHandler){
        this.parentMessageHandler = parentMessageHandler;
    }

    public void run(){
        while(!this.parentMessageHandler.socket.isClosed()){
            String sender = this.parentMessageHandler.getParentLeader().getParentNode().getIp();
            String receiver = this.parentMessageHandler.getFollowerIp();
            Message heartbeat = new Message(sender, receiver, "heartbeat", MessageType.HEARTBEAT);
            this.parentMessageHandler.sendMessage(heartbeat);
            Util.sleep(Config.heartbeatInterval);
        }
    }
}
