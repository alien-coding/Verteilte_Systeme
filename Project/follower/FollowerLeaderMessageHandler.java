package Project.follower;
import java.net.Socket;
import java.time.Instant;

import Project.CheckHeartbeat;
import Project.Node;
import Project.Role;
import Project.message.Message;
import Project.message.MessageHandler;
import Project.message.MessageType;

public class FollowerLeaderMessageHandler extends MessageHandler{
    private Instant lastHeartbeat;
    private CheckHeartbeat checker;

    /**
     * Executed by follower, handles connection with leader
     * @param parentNode
     * @param newConnection
     */
    public FollowerLeaderMessageHandler(Node parentNode, Socket newConnection){
        super(parentNode, newConnection);
        this.checker = new CheckHeartbeat(this);
    }

    public void run(){
        //TODO: Heartbeat handling here!!
        checker.start();
        while(!this.socket.isClosed()){
            this.receiveMessagesRoutine();
        }
    }

    public void leaderTimedOut(){
        try {
            this.socket.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        this.getParentNode().setRole(Role.UNKNOWN);
    }
    
    @Override
    protected void handleInitializeMessage(Message message){
        System.out.println("Answer not implemented");
    }
    
    @Override
    protected void handleHeartbeatMessage(Message message){
        this.lastHeartbeat = message.getTime();
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), " ...  ", MessageType.ACK);
        this.sendMessage(answer);
    }
    
    @Override
    protected void handleSyncNodeListMessage(Message message){
        System.out.println("Answer not implemented");
    }

    @Override
    protected void handleNavigationMessage(Message message){
        System.out.println("Answer not implemented");
    }

    public Instant getLastHeartbeat(){return this.lastHeartbeat;}
}
