package Project.leader;

import Project.Config;
import Project.Util;
import Project.message.Message;
import Project.message.MessageType;

public class Heartbeat extends Thread {
    private LeaderMessageHandler parentMessageHandler;
    private Boolean gotAnswer;

    /**
     * 
     * @param leader Leader who is initializing Heartbeat
     * @param HEARTBEAT_INTERVAL in s
     */
    public Heartbeat(LeaderMessageHandler parentMessageHandler){
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
