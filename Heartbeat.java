import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import Message.Message;
import Message.MessageType;

public class Heartbeat extends Thread {
    private LeaderMessageHandler parentMessageHandler;
    private int heartbeatInterval;

    /**
     * 
     * @param leader Leader who is initializing Heartbeat
     * @param heartbeatInterval in s
     */
    public Heartbeat(LeaderMessageHandler parentMessageHandler, int heartbeatInterval){
        this.parentMessageHandler = parentMessageHandler;
        this.heartbeatInterval = heartbeatInterval;
    }

    public void run(){
        while(!this.parentMessageHandler.socket.isClosed()){
            String sender = this.parentMessageHandler.getParentLeader().getParentNode().getIp();
            InetSocketAddress test = (InetSocketAddress) this.parentMessageHandler.socket.getRemoteSocketAddress();
            String receiver = test.getAddress().getHostAddress();
            System.out.println("sender: " + sender + " receiver: " + receiver);
            Message heartbeat = new Message(sender, receiver, "heartbeat", MessageType.HEARTBEAT);
            this.parentMessageHandler.sendMessage(heartbeat);
            try {
                TimeUnit.SECONDS.sleep(this.heartbeatInterval);
            } catch (InterruptedException e) {
                System.err.println(e.toString());
            }
        }
    }
}
