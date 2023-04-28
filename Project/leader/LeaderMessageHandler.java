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
        while(!this.socket.isClosed()){
            Message message = this.readMessage();
            System.out.println(this.parentNode.getIp() + " received a message: " + message.getPayload());
            // Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ); //TODO: change acc type
            switch (message.getType()) {
                case READ:
                    this.messageRead(message);
                    break;
                case WRITE:
                    this.messageWrite(message);
                    break;
                case INITIALIZE:
                    this.messageInitialize(message);
                    break;
                case HEARTBEAT:
                    this.messageHeartbeat(message);
                    break;
                case UNKNOWN:
                    this.messageUnknown(message);
                    break;                    
                default:
                    break;
            }
        }
    }

    private void messageRead(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ);
        this.sendMessage(answer);
    }

    private void messageWrite(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ);
        this.sendMessage(answer);
    }

    private void messageInitialize(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ);
        this.sendMessage(answer);
    }

    private void messageHeartbeat(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ);
        System.out.println(this.parentNode.getIp() + " received type " + message.getType() + " message: " + message.getPayload());
        this.sendMessage(answer);
    }

    private void messageUnknown(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ);
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
