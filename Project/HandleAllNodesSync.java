package Project;

import java.util.HashMap;

import Project.leader.Leader;
import Project.leader.LeaderMessageHandler;
import Project.message.Message;
import Project.message.MessageType;

public class HandleAllNodesSync extends Thread {
    private Leader leader;
    private HashMap<String, NodeSaver> allKnwonNodes;

    public HandleAllNodesSync(Leader leader, HashMap<String, NodeSaver> allKnwonNodes){
        this.leader = leader;
        this.allKnwonNodes = allKnwonNodes;
    }

    public void run(){
        String sender = this.leader.getParentNode().getIp();
        String receiver = "";//connection.getFollowerIp();
        Message toSend = new Message(sender, receiver, this.allKnwonNodes, MessageType.SYNC_NODE_LIST);
        Util.sleep(50);
        for (LeaderMessageHandler connection : this.leader.getConnections()) {
            System.out.println("sending " + toSend.getPayload() + " to: " + connection.getFollowerIp());
            connection.sendMessage(toSend);
        }
    }
}