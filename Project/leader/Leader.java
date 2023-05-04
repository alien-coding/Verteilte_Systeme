package project.leader;

import java.util.LinkedList;

import project.Config;
import project.Node;
import project.Util;
import project.helpers.TrafficArea;
import project.helpers.TrafficControlLogic;
import project.message.Message;
import project.message.MessageType;


public class Leader extends Thread{
    private String addressForClients;
    private int portForClients;
      
    private Node parentNode;
    private LinkedList<LeaderFollowerMessageHandler> nodeConnections = new LinkedList<LeaderFollowerMessageHandler>(); //all accepted connections are added here

    /**
     * Starts node, initializes the area and logic of parent Node.
     * @param node the parent node which is creating this Leader
     * @param addressForClients the address to which clients can connect.
     * @param portForClients the port to which clients can connect. Should be different to the port for network functionality.
     */
    public Leader(Node node, String addressForClients, int portForClients){
        this.parentNode = node;
        this.addressForClients = addressForClients;
        this.portForClients = portForClients;
        try {
            TrafficArea area = new TrafficArea(Config.MAX_PER_NODE, Config.SIZE_X, Config.SIZE_Y);
            this.parentNode.setArea(area);
            this.parentNode.setLogic(new TrafficControlLogic(area));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Starts FollowerRoutine first to be able to let followers connect as fast as possible after starting.
     * Then accepts clients and handles them.
     */
    public void run(){
        FollowerRoutine followerRoutine = new FollowerRoutine(this);
        ClientRoutine clientRoutine = new ClientRoutine(this);
        followerRoutine.start();
        clientRoutine.start();
    }

    /**
     * Sends message to all followers with the list of all nodes in the network. 
     * Triggered when follower connects to leader or disconnects.
     */
    public void updateNodeList(){
        Message message = new Message(this.parentNode.getIp(), "", this.parentNode.getAllKnownNodes().clone(), MessageType.SYNC_NODE_LIST);
        Util.sleep(10);  //so sending message does not happen in exact same time as first heartbeat (triggered by messageHandler.start)
        for (LeaderFollowerMessageHandler connection : nodeConnections) {
            message.setReceiver(connection.getFollowerIp());
            connection.sendMessage(message);
        }
    }

    public LinkedList<LeaderFollowerMessageHandler> getNodeConnections(){return this.nodeConnections;}
    public void setNodeConnections(LinkedList<LeaderFollowerMessageHandler> connections){this.nodeConnections = connections;}
    public Node getParentNode(){return this.parentNode;}
    public String getAddressForClients() {return this.addressForClients;}
    public int getPortForClients() {return this.portForClients;}
}
