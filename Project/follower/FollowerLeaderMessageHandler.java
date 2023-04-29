package Project.follower;
import java.net.Socket;
import java.time.Instant;
import java.util.HashMap;

import Project.Node;
import Project.Role;
import Project.Util;
import Project.NodeSaver;
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
        checker.start();
        while(!this.socket.isClosed()){
            this.receiveMessagesRoutine();
        }
        this.getParentNode().setRole(Role.UNKNOWN); //When leader socket shuts down, init the system again
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
        this.lastHeartbeat = Instant.now(); //not using message.getTime() because time of arrival is key, not time of message creation
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Heartbeat received", MessageType.ACK);
        this.sendMessage(answer);
    }
    
    @Override
    protected void handleSyncNodeListMessage(Message message){
        System.out.println("Sync node list");
        try {
            HashMap<String, NodeSaver> allKnownNodes = (HashMap<String, NodeSaver>) message.getPayload();
            if(this.parentNode.getAllKnownNodes().size() <= allKnownNodes.size()){
                this.parentNode.setAllKnownNodes(allKnownNodes);
                System.out.println("set new list");
                // Message answer = new Message(this.parentNode.getIp(), message.getSender(), "", MessageType.SUCCESS);
                // this.sendMessage(answer);
            }
            else{
                System.out.println("outdated");
                // Message answer = new Message(this.parentNode.getIp(), message.getSender(), "New Sync List was outdated", MessageType.ERROR);
                // this.sendMessage(answer);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        
        // System.out.println("Answer not implemented: Sync Node List");
    }

    @Override
    protected void handleNavigationMessage(Message message){
        System.out.println("Answer not implemented");
    }

    public Instant getLastHeartbeat(){return this.lastHeartbeat;}
}
