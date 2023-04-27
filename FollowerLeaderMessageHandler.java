import java.io.IOException;
import java.net.Socket;

import Message.Message;
import Message.MessageType;

public class FollowerLeaderMessageHandler extends MessageHandler{

    public FollowerLeaderMessageHandler(Node parentNode, Socket newConnection){
        super(parentNode, newConnection);
    }

    public void run(){
        //TODO: Heartbeat handling here!!
        while(!this.socket.isClosed()){
            Message message = this.readMessage();
            System.out.println(this.parentNode.getIp() + " received a " + message.getType() + " message: " + message.getPayload());
            if(message.getType() == MessageType.HEARTBEAT){
                Message answer = new Message(this.parentNode.getIp(), message.getSender(), "", MessageType.UNKNOWN);
                try {
                    this.outputStream.writeObject(answer);
                } catch (IOException e) {
                    System.err.println(e.toString());
                }
            }
        }
    }
}
