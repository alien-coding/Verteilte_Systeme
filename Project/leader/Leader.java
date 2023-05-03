package project.leader;

import java.util.LinkedList;
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

    public Leader(Node node, String addressForClients, int portForClients){
        this.parentNode = node;
        this.addressForClients = addressForClients;
        this.portForClients = portForClients;
        TrafficArea area = new TrafficArea((short) 2, (short) 10, (short) 10);
        this.parentNode.setArea(area);
        this.parentNode.setLogic(new TrafficControlLogic(area));
    }

    public void run(){
        ClientRoutine clientRoutine = new ClientRoutine(this);
        FollowerRoutine followerRoutine = new FollowerRoutine(this);
        clientRoutine.start();
        followerRoutine.start();
    }

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
