import java.util.concurrent.TimeUnit;

import Message.Message;
import Message.MessageType;

public class Heartbeat extends Thread {
    private Leader parentLeader;
    private int heartbeatInterval;

    /**
     * 
     * @param leader Leader who is initializing Heartbeat
     * @param heartbeatInterval in s
     */
    public Heartbeat(Leader leader, int heartbeatInterval){
        this.parentLeader = leader;
        this.heartbeatInterval = heartbeatInterval;
    }

    public void run(){
        try {
            TimeUnit.SECONDS.sleep(this.heartbeatInterval);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
        for (MessageHandler connection : this.parentLeader.getConnections()) {
            Message heartbeat = new Message(this.parentLeader.getParentNode().getIp(), 
                                            connection.socket.getInetAddress().toString(), 
                                    "heartbeat", MessageType.HEARTBEAT);
            connection.sendMessage(heartbeat);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.err.println(e.toString());
            }
        }
    }
}
