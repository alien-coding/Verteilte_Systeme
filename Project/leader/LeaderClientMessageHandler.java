package project.leader;

import java.net.InetSocketAddress;
import java.net.Socket;

import project.Node;
import project.NodeSaver;
import project.Role;
import project.message.Message;
import project.message.MessageHandler;
import project.message.MessageType;

public class LeaderClientMessageHandler extends MessageHandler {
    private Leader parentLeader;
    private String clientIp;
    private int clientPort;

    public LeaderClientMessageHandler(Node parentNode, Socket newConnection, Leader parentLeader){
        super(parentNode, newConnection);
        this.parentLeader = parentLeader;
    }

    @Override
    protected void handleInitializeMessage(Message message) {
        System.out.println("noch net fertig");
    }

    @Override
    protected void handleHeartbeatMessage(Message message) {
        System.out.println("noch net fertig");
    }

    @Override
    protected void handleSyncNodeListMessage(Message message) {
        System.out.println("noch net fertig");
    }

    @Override
    protected void handleNavigationMessage(Message message) {
        System.out.println("noch net fertig");
    }

    public Boolean registerConnection(){
        Message message = this.readMessage();
        System.out.println(this.parentNode.getIp() + " received a "+ message.getType() + " message: " + message.getPayload());
        if(message.getType() == MessageType.INITIALIZE){
            try {
                InetSocketAddress clientAddress = (InetSocketAddress) message.getPayload();
                this.clientIp = clientAddress.getHostName();
                this.clientPort = clientAddress.getPort();
                System.out.println(this.parentLeader.getParentNode().getIp() + ": Leader registered " + this.clientIp);

                if(this.clientIp.contains("127.0.0.")){
                    String payload = "Registered " + this.clientIp + " as Follower";
                    Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.SUCCESS); 
                    this.sendMessage(answer);
                    
                    this.isClient = false;
                    NodeSaver newFollower = new NodeSaver(Role.FOLLOWER, this.clientIp, this.clientPort);
                    this.parentLeader.getParentNode().addToAllKnownNodes(this.clientIp, newFollower);
                    this.parentLeader.getNodeConnections().add(this);
                    this.parentLeader.updateNodeList();
                }
                else if(this.clientIp.contains("127.0.1.")){
                    this.isClient = true;
                    String payload = "Registered " + this.clientIp + " as Client";
                    Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.SUCCESS); 
                    this.sendMessage(answer);
                }
                
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
}
