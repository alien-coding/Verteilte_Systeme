package Project.follower;
import java.net.Socket;

import Project.Node;
import Project.message.Message;
import Project.message.MessageHandler;
import Project.message.MessageType;

public class FollowerLeaderMessageHandler extends MessageHandler{

    /**
     * Executed by follower, handles connection with leader
     * @param parentNode
     * @param newConnection
     */
    public FollowerLeaderMessageHandler(Node parentNode, Socket newConnection){
        super(parentNode, newConnection);
    }

    public void run(){
        //TODO: Heartbeat handling here!!
        while(!this.socket.isClosed()){
            Message message = this.readMessage();
            System.out.println(this.parentNode.getIp() + " received a " + message.getType().toString() + " message: " + message.getPayload());
            if(message.getType() == MessageType.HEARTBEAT){
                Message answer = new Message(this.parentNode.getIp(), message.getSender(), " ...  ", MessageType.HEARTBEAT);
                this.sendMessage(answer);
            }
            else if(message.getType() == MessageType.UNKNOWN){
                Message answer = new Message(this.parentNode.getIp(), message.getSender(), " ...  ", MessageType.UNKNOWN);
                this.sendMessage(answer);
            }
            else{
                System.out.println("not covered answer");
            }
        }
    }
}
