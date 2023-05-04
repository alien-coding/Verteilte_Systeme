package project.leader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import project.Role;
import project.helpers.Coordinate;
import project.Node;
import project.NodeSaver;
import project.message.*;

public class LeaderFollowerMessageHandler extends MessageHandler {
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
    public LeaderFollowerMessageHandler(Node parentNode, Socket newConnection, Leader parentLeader){
        super(parentNode, newConnection);
        this.parentLeader = parentLeader;
        this.heartbeat = new Heartbeat(this);
    }

    public void run(){
        this.heartbeat.start();
        while(!this.socket.isClosed()){
            this.receiveMessagesRoutine();
        }
        this.removeLostFollower();
    }

    @Override
    protected void handleInitializeMessage(Message message){
        //TODO change answer because init is handled elsewhere
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        this.sendMessage(answer);
    }
    
    //If leader receives Heartbeat Message, something has gone wrong. It should only receive ACK answers to its own Heartbeat messages.
    @Override
    protected void handleHeartbeatMessage(Message message){
        String payload = "Don't send heartbeats to the leader. If responding to one, use ACK.";
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.ERROR);
        this.sendMessage(answer);
    }

    @Override
    protected void handleSyncNodeListMessage(Message message){
        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.SUCCESS);
        this.sendMessage(answer);
    }

    @Override
    protected void handleNavigationMessage(Message message){
        try {
            Coordinate[] payload = (Coordinate[]) message.getPayload();
            try {
                if(payload.length == 2){
                    if(this.parentNode.getArea().getPosition(message.getSender()) == null){
                        this.parentNode.getArea().place(message.getSender(), payload[0]);
                    }
                    if(!this.parentNode.getArea().getPosition(message.getSender()).compare(payload[0])){
                        this.parentNode.getArea().remove(message.getSender(), this.parentNode.getArea().getPosition(message.getSender()));
                        this.parentNode.getArea().place(message.getSender(), payload[0]);
                    }
                    
                    Coordinate nextStep = this.parentNode.getLogic().move(message.getSender(), payload[1]);
                    if(!nextStep.compare(payload[0])){
                        Message answer = new Message(this.parentNode.getIp(), message.getSender(), nextStep, MessageType.SUCCESS); 
                        this.sendMessage(answer);
                    }
                    else{
                        Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Can't make move to next field", MessageType.ERROR); 
                        this.sendMessage(answer);
                    }
                }
                else{
                    System.out.println("Payload not containing all information");
                    Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Please send navigation message with Array of 0: your position and 1: your destination", MessageType.ERROR); 
                    this.sendMessage(answer);
                }
            } catch (Exception e) {
                System.err.println("Move not possible: " + e.toString());
                Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Move is not possible", MessageType.ERROR); 
                this.sendMessage(answer);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    //leader has to implement this method by its own cause it is waiting for the ack messages of the clients.
    @Override
    protected void handleAckMessage(Message message){
        this.heartbeat.setGotAnswer(true);
    }

    public Boolean registerConnection(){
        Message message = this.readMessage();
        System.out.println(this.parentNode.getIp() + " received a "+ message.getType() + " message: " + message.getPayload());
        if(message.getType() == MessageType.INITIALIZE){
            try {
                InetSocketAddress clientAddress = (InetSocketAddress) message.getPayload();
                this.followerIp = clientAddress.getHostName();
                this.followerPort = clientAddress.getPort();

                if(this.followerIp.contains("127.0.0.")){
                    System.out.println(this.parentLeader.getParentNode().getIp() + ": Leader registered " + this.followerIp);                
                    String payload = "Registered " + this.followerIp + " as Follower";
                    Message answer = new Message(this.parentNode.getIp(), message.getSender(), payload, MessageType.SUCCESS); 
                    this.sendMessage(answer);
                    
                    NodeSaver newFollower = new NodeSaver(Role.FOLLOWER, this.followerIp, this.followerPort);
                    this.parentLeader.getParentNode().addToAllKnownNodes(this.getFollowerIp(), newFollower);
                    this.parentLeader.getNodeConnections().add(this);
                    this.parentLeader.updateNodeList();
                    return true;
                }
                else {
                    System.out.println(this.parentLeader.getParentNode().getIp() + ": Leader rejected " + this.followerIp);
                    String payload = "Please connect to " + this.parentLeader.getAddressForClients() + ":";
                    payload += this.parentLeader.getPortForClients() + " for client functionality";
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

    public void followerTimedOut(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.removeLostFollower();
    }

    private void removeLostFollower(){
        this.parentLeader.getNodeConnections().remove(this);
        this.parentLeader.getParentNode().getAllKnownNodes().remove(this.followerIp);
        this.parentLeader.updateNodeList();
    }

    public Leader getParentLeader() {return this.parentLeader;}
    public String getFollowerIp() {return this.followerIp;}
    public void setFollowerIp(String followerIp) {this.followerIp = followerIp;}
    public int getFollowerPort() {return this.followerPort;}
    public void setFollowerPort(int followerPort) {this.followerPort = followerPort;}
}
