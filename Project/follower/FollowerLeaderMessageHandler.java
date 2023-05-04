package project.follower;
import java.net.Socket;
import java.time.Instant;
import java.util.HashMap;

import project.Node;
import project.NodeSaver;
import project.Role;
import project.message.Message;
import project.message.MessageHandler;
import project.message.MessageType;

/**
 * Message Handler for Followers to connect with Leader. Only for the follower side.
 * Run method starts the heartbeat checker which checks if leader is no more sending heartbeats and aborts if necessary. 
 */
public class FollowerLeaderMessageHandler extends MessageHandler{
    private Instant lastHeartbeat;
    private CheckHeartbeat checker;
    private Follower parentFollower;

    /**
     * Executed by follower, handles connection with leader
     * @param parentNode
     * @param newConnection
     */
    public FollowerLeaderMessageHandler(Follower parentFollower, Node parentNode, Socket newConnection){
        super(parentNode, newConnection);
        this.parentFollower = parentFollower;
        this.checker = new CheckHeartbeat(this);
    }

    /**
     * When connection to leader is lost (socket then gets closed), go back to Unknown and figure out new leader.
     */
    public void run(){
        checker.start();
        while(!this.socket.isClosed()){
            this.receiveMessagesRoutine();
        }
        this.getParentNode().setRole(Role.UNKNOWN); //When leader socket shuts down, init the system again
    }

    /**
     * Overriding the message because of forwarding functionality for messages. 
     * When navigation message is received by a FollowerClientMessageHandler, it is forwarded to Leader. 
     * Response by leader is then handled here. So Leader sends message with client as recipient.
     * Follower realizes message is not for it self and tries to forward it. 
     */
    @Override
    protected void receiveMessagesRoutine(){
        try {
            Message message = this.readMessage();
            System.out.println(this.ip + " received a " + message.getType().toString() + " message: " + message.getPayload());
            if(this.parentNode.getIp().equals(message.getReceiver())){
                switch (message.getType()) {
                    case INITIALIZE:
                        this.handleInitializeMessage(message);
                        break;
                    case HEARTBEAT:
                        this.handleHeartbeatMessage(message);
                        break;
                    case SYNC_NODE_LIST:
                        this.handleSyncNodeListMessage(message);
                        break;   
                    case NAVIGATION:
                        this.handleNavigationMessage(message);
                        break;
                    case SUCCESS:
                        this.handleSuccessMessage(message);
                        break;
                    case ERROR:
                        this.handleErrorMessage(message);
                        break;
                    case ACK:
                        this.handleAckMessage(message);
                        break;
                    default:
                        break;
                }
            }
            else{
                for (FollowerClientMessageHandler messageHandler : this.parentFollower.getClientConnections()) {
                    if(messageHandler.getClientIp().equals((message.getReceiver()))){
                        messageHandler.sendMessage(message);
                        System.out.println(this.parentNode.getIp() + " forwarded message " + message.getPayload() + " to " + messageHandler.getClientIp());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * When Leader times out (no heartbeats received), this method is called by the heartbeat checker.
     * Leader connection is quit here (leader is dead) and role is set to Unknown to figure out new leader.
     */
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
    
    /**
     * Sets time of last heartbeat received. This time is checked by heartbeat checker.
     * Acknowledgement is send to let leader know follower is still alive.
     */
    @Override
    protected void handleHeartbeatMessage(Message message){
        this.lastHeartbeat = Instant.now(); //not using message.getTime() because time of arrival is key, not time of message creation
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Heartbeat received", MessageType.ACK);
        this.sendMessage(answer);
    }
    
    /**
     * When leader initializes new Follower successfully, this message type is sent by it.
     * Set own known nodes to the new list.
     */
    @Override
    protected void handleSyncNodeListMessage(Message message){
        try {
            HashMap<String, NodeSaver> updatedNodeList = (HashMap<String, NodeSaver>) message.getPayload();
            if(updatedNodeList.size() >= 2){    //Minimum length of list must be self and leader, without the node cannot exist
                this.parentNode.setAllKnownNodes(updatedNodeList);
                System.out.println(this.parentNode.getIp() + " updated list of all nodes");
            }
            else{
                System.out.println(this.parentNode.getIp() + " is not setting new Node list. Size smaller than 2");
            }
        } catch (Exception e) {
            System.err.println("Error while setting new node list:");
            System.err.println(e.toString());
        }
    }

    /**
     * Navigation messages are only sent by Clients, so not handled here. This case is an error case.
     */
    @Override
    protected void handleNavigationMessage(Message message){
        System.out.println("Answer not implemented");
    }

    public Instant getLastHeartbeat(){return this.lastHeartbeat;}
}
