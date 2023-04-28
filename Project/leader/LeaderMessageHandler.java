package Project.leader;
import java.net.InetSocketAddress;
import java.net.Socket;

import Project.Node;
import Project.message.*;

public class LeaderMessageHandler extends MessageHandler {
    private Heartbeat heartbeat;
    private Leader parentLeader;
    private String followerIp;
    private int followerPort;

    /**
     * Initializes input and output streams on creation, since every Message handler is 
     * responsible for one single connection. Object is only called for new accepted connections,
     * purpose is therefore receiving messages. 
     * @param parentNode node which is parent of this process, necessary for handling messages
     * @param newConnection the new connection that has been accepted and has to be initialized
     */
    public LeaderMessageHandler(Node parentNode, Socket newConnection, Leader parentLeader){
        super(parentNode, newConnection);
        this.parentLeader = parentLeader;
        this.heartbeat = new Heartbeat(this);
    }

    public void run(){
        this.getInitMessage();
        this.heartbeat.start();
        this.receiveMessagesRoutine();
    }

    @Override
    protected void handleInitializeMessage(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        this.sendMessage(answer);
    }
    
    @Override
    protected void handleHeartbeatMessage(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        System.out.println(this.parentNode.getIp() + " received type " + message.getType() + " message: " + message.getPayload());
        System.out.println("ERRRRORRRRRR");
        this.sendMessage(answer);
    }

    @Override
    protected void handleSyncNodeListMessage(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        this.sendMessage(answer);
    }

    @Override
    protected void handleNavigationMessage(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        this.sendMessage(answer);
    }

    @Override
    protected void handleAckMessage(Message message){
        //TODO: implement heartbeat acknowledgement here
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        this.sendMessage(answer);
    }
    

    private void getInitMessage(){
        Message message = this.readMessage();
        System.out.println(this.parentNode.getIp() + " received a message: " + message.getPayload());
        if(message.getType() == MessageType.INITIALIZE){
            InetSocketAddress clientAddress = (InetSocketAddress) message.getPayload();
            this.followerIp = clientAddress.getHostName();
            this.followerPort = clientAddress.getPort();

            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Registered you as follower", MessageType.INITIALIZE); //TODO: change acc type
            this.sendMessage(answer);
        }
        else{
            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Please send init Message", MessageType.ERROR); //TODO: change acc type
            this.sendMessage(answer);
            this.getInitMessage(); //give registering node new try to successfully connect
        }
    }

    public Leader getParentLeader() {return this.parentLeader;}
    public String getFollowerIp() {return this.followerIp;}
    public void setFollowerIp(String followerIp) {this.followerIp = followerIp;}
    public int getFollowerPort() {return this.followerPort;}
    public void setFollowerPort(int followerPort) {this.followerPort = followerPort;}
}
