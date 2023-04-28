import java.net.Socket;

import Message.*;

public class LeaderMessageHandler extends MessageHandler {
    private Heartbeat heartbeat;
    private Leader parentLeader;
    /**
     * Initializes input and output streams on creation, since every Message handler is 
     * responsible for one single connection. Object is only called for new accepted connections,
     * purpose is therefore receiving messages. 
     * @param parentNode node which is parent of this process, necessary for handling messages
     * @param newConnection the new connection that has been accepted and has to be initialized
     */
    public LeaderMessageHandler(Node parentNode, Socket newConnection, Leader parentLeader){
        super(parentNode, newConnection);
        this.parentLeader = parentLeader;
        this.heartbeat = new Heartbeat(this.parentLeader, 1);
    }

    public void run(){
        this.getInitMessage();
        this.heartbeat.run();
        while(!this.socket.isClosed()){
            Message message = this.readMessage();
            System.out.println(this.parentNode.getIp() + " received a message: " + message.getPayload());
            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "message answer", MessageType.READ); //TODO: change acc type
            Boolean sendAnswer = true;
            switch (message.getType()) {
                case READ:
                    break;
                case WRITE:
                    break;
                case INITIALIZE:
                    break;
                case HEARTBEAT:
                    System.out.println("received Heartbeat from Client");
                    sendAnswer = false;
                    break;
                case UNKNOWN:
                    break;
                default:
                    break;
            }

            if(sendAnswer){
                this.sendMessage(answer); //since this is an answer, no need to get answer
            }
        }
    }
    private void getInitMessage(){
        Message message = this.readMessage();
        System.out.println(this.parentNode.getIp() + " received a message: " + message.getPayload());
        if(message.getType() == MessageType.INITIALIZE){
            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Registered you as follower", MessageType.INITIALIZE); //TODO: change acc type
            this.sendMessage(answer);
        }
        else{
            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "Please send init Message", MessageType.ERROR); //TODO: change acc type
            this.sendMessage(answer);
            this.getInitMessage(); //give registering node new try to successfully connect
        }
    }
}
