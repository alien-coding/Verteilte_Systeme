package project.leader;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

import project.Node;
import project.helpers.Coordinate;
import project.message.Message;
import project.message.MessageHandler;
import project.message.MessageType;

public class LeaderClientMessageHandler extends MessageHandler {
    private Leader parentLeader;
    private String clientIp;
    private int clientPort;
    private String uniqueId;

    public LeaderClientMessageHandler(Node parentNode, Socket newConnection, Leader parentLeader){
        super(parentNode, newConnection);
        this.parentLeader = parentLeader;
        this.uniqueId = UUID.randomUUID().toString();
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
        try {
            Coordinate[] payload = (Coordinate[]) message.getPayload();
            this.getParentLeader().getParentNode().getArea().place(this.uniqueId, payload[0]);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public Boolean registerConnection(){
        Message message = this.readMessage();
        System.out.println(this.parentNode.getIp() + " received a "+ message.getType() + " message: " + message.getPayload());
        if(message.getType() == MessageType.INITIALIZE){
            try {
                InetSocketAddress clientAddress = (InetSocketAddress) message.getPayload();
                this.clientIp = clientAddress.getHostName();
                this.clientPort = clientAddress.getPort();

                if(this.clientIp.contains("127.0.1.")){
                    System.out.println(this.parentLeader.getParentNode().getIp() + ": Leader registered " + this.clientIp);

                    String payload = "Registered " + this.clientIp + " as Client";
                    Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.SUCCESS); 
                    this.sendMessage(answer);
                    return true;
                }
                else {
                    System.out.println(this.parentLeader.getParentNode().getIp() + ": Leader rejected " + this.clientIp);
                    String payload = "Please connect to " + this.parentLeader.getParentNode().getIp() + ":";
                    payload += this.parentLeader.getParentNode().getPort() + " for network functionality";
                    Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.ERROR); 
                    this.sendMessage(answer);
                    return false;
                }

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

    public Leader getParentLeader() {return this.parentLeader;}
    public void setParentLeader(Leader parentLeader) {this.parentLeader = parentLeader;}
    public String getClientIp() {return this.clientIp;}
    public void setClientIp(String clientIp) {this.clientIp = clientIp;}
    public int getClientPort() {return this.clientPort;}
    public void setClientPort(int clientPort) {this.clientPort = clientPort;}
}
