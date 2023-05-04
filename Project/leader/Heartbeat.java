package project.leader;

import project.Config;
import project.Util;
import project.message.Message;
import project.message.MessageType;

/**
 * Heartbeat handles the sending heartbeat functionality and therefore is only used by the leader.
 * It is based on the already existing connection from the LeaderFollowerMessageHandler.
 * For every Leader-Follower connection, one Heartbeat object is created and run.
 * When only one Message is not answered with an acknowledge, the connection is interrupted and the follower is forgotten.
 */
public class Heartbeat extends Thread {
    private LeaderFollowerMessageHandler parentMessageHandler;
    private Boolean gotAnswer;

    /**
     * Heartbeat sender is always coupled to a LeaderFollowerMessageHandler. 
     * This is to use the already existing connection between these two nodes.
     * @param parentMessageHandler
     */
    public Heartbeat(LeaderFollowerMessageHandler parentMessageHandler){
        this.parentMessageHandler = parentMessageHandler;
        this.gotAnswer = true; //so Heartbeat is sent initially (Object only created when init is completed)
    }

    public void run(){
        while(!this.parentMessageHandler.getSocket().isClosed()){
            String sender = this.parentMessageHandler.getParentLeader().getParentNode().getIp();
            String receiver = this.parentMessageHandler.getFollowerIp();
            if(gotAnswer){
                Message heartbeat = new Message(sender, receiver, "heartbeat", MessageType.HEARTBEAT);
                this.parentMessageHandler.sendMessage(heartbeat);
                this.gotAnswer = false;
                Util.sleep(Config.HEARTBEAT_INTERVAL);
            }
            else{
                System.out.println(sender + ": " + receiver + " did not ack last heartbeat. Quitting connection");
                this.parentMessageHandler.followerTimedOut(); //closes socket, so while loop is exited
            }
        }
    }
    public Boolean getGotAnswer() {return this.gotAnswer;}
    public void setGotAnswer(Boolean gotAnswer) {this.gotAnswer = gotAnswer;}
}
