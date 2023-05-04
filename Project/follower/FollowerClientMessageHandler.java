package project.follower;
import java.net.InetSocketAddress;
import java.net.Socket;

import project.Node;
import project.message.Message;
import project.message.MessageHandler;
import project.message.MessageType;

/**
 * Message Handler for Followers that connect with Clients. Only for the follower side.
 * run method waits for init message, then starts the receiving messages routine.
 */
public class FollowerClientMessageHandler extends MessageHandler{
    private Follower parentFollower;
    private String clientIp;
    private int clientPort;

    public FollowerClientMessageHandler(Follower parentFollower, Node parentNode, Socket newConnection){
        super(parentNode, newConnection);
        this.parentFollower = parentFollower;
    }

    public void run(){
        this.registerConnection();
        while(!this.socket.isClosed()){
            this.receiveMessagesRoutine();
        }
        this.parentFollower.getClientConnections().remove(this);
    }

    /**
     * Since the actual initialize Message is handled in registerConnection(), 
     * this method is for handling unwanted (later following) initialize messages.
     */
    @Override
    protected void handleInitializeMessage(Message message){
        System.out.println("Answer not implemented");
    }
    
    /**
     * No Heartbeats that are sent from Clients, this is an error case
     */
    @Override
    protected void handleHeartbeatMessage(Message message){
        System.out.println("Answer not implemented");
    }
    
    /**
     * Same as Heartbeats, functionality does not exist for Clients, this is an error case.
     */
    @Override
    protected void handleSyncNodeListMessage(Message message){
        System.out.println("Answer not implemented");
    }
    
    /**
     * Forward the Navigation Messages to the leader because leader is the one handling the functionality.
     */
    @Override
    protected void handleNavigationMessage(Message message){
        this.parentFollower.getConnectionToLeader().sendMessage(message);
    }

    /**
     * Handles initialize Message. Waits for init message (first message) and complains if it is not a correct initialize.
     * @return true when successfully initialized, false if not. 
     */
    public Boolean registerConnection(){
        Message message = this.readMessage();
        System.out.println(this.parentNode.getIp() + " received a "+ message.getType() + " message: " + message.getPayload());
        if(message.getType() == MessageType.INITIALIZE){
            try {
                InetSocketAddress clientAddress = (InetSocketAddress) message.getPayload();
                this.clientIp = clientAddress.getHostName();
                this.clientPort = clientAddress.getPort();
                System.out.println(this.ip + ": Leader registered " + this.clientIp);
                
                String payload = this.ip + " registered " + this.clientIp + " as Client";
                Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.SUCCESS); 
                this.sendMessage(answer);
                
                return true;

            } catch (Exception e) {
                System.out.println("Init message failed");
                String payload = "Insert INetSocketAddress of own IP and Port in payload.";
                Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.ERROR); 
                this.sendMessage(answer);
                return false;
            }
        }
        else{
            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Please send init Message", MessageType.ERROR);
            this.sendMessage(answer);
            return false;
        }
    }

    public Follower getParentFollower() {return this.parentFollower;}
    public void setParentFollower(Follower parentFollower) {this.parentFollower = parentFollower;}
    public String getClientIp() {return this.clientIp;}
    public void setClientIp(String clientIp) {this.clientIp = clientIp;}
    public int getClientPort() {return this.clientPort;}
    public void setClientPort(int clientPort) {this.clientPort = clientPort;}
}
