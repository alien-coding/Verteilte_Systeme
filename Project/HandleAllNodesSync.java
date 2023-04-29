package Project;

import Project.leader.Leader;
import Project.leader.LeaderMessageHandler;
import Project.message.Message;
import Project.message.MessageType;

public class HandleAllNodesSync extends Thread {
    private Leader leader;

    public HandleAllNodesSync(Leader leader){
        this.leader = leader;
    }

    public void run(){
        Util.sleep(50);
        for (LeaderMessageHandler connection : this.leader.getConnections()) {
            String sender = this.leader.getParentNode().getIp();
            String receiver = connection.getFollowerIp();
            Message toSend = new Message(sender, receiver, this.leader.getParentNode().getAllKnownNodes(), MessageType.SYNC_NODE_LIST);
            connection.sendMessage(toSend);   
        }
    }
}