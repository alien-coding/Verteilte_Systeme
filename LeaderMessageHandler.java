import java.io.*;
import java.net.Socket;

import Message.*;

public class LeaderMessageHandler extends MessageHandler {
    private Leader parentLeader;

    /**
     * Initializes input and output streams on creation, since every Message handler is 
     * responsible for one single connection. Object is only called for new accepted connections,
     * purpose is therefore receiving messages. Handling is different if parent node is leader or follower.
     * @param parentNode node which is parent of this process, nescesary for handling messages
     * @param newConnection the new connection that has been accepted and has to be initialized
     */
    public LeaderMessageHandler(Node parentNode, Leader parentLeader, Socket newConnection){
        super(parentNode, newConnection);
        this.parentLeader = parentLeader;
        this.initializeStreams(newConnection);
    }

    public void run(){
        while(!this.socket.isClosed()){
            Message message = this.readMessage();
            System.out.println(this.parentNode.getIp() + " received a message: " + message.getPayload().toString());
            Message answer = new Message(this.parentNode.getIp(), message.getSender(), "", MessageType.UNKNOWN);
            switch (message.getType()) {
                case READ:
                    break;
                case WRITE:
                    break;
                case INITIALIZE:
                    break;
                case UNKNOWN:
                    break;
                default:
                    break;
            }

            try {
                this.outputStream.writeObject(answer);
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }
    }
}
