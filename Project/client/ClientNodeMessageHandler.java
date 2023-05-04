package project.client;

import java.net.InetSocketAddress;
import java.net.Socket;
import project.message.Message;
import project.message.MessageHandler;
import project.message.MessageType;

public class ClientNodeMessageHandler extends MessageHandler{
    private Client parentClient;
    private Object lastAnswer;

    public ClientNodeMessageHandler(Socket socket, String  ip, int port, Client parenClient){
        super(socket, ip, port);
        this.parentClient = parenClient;
        this.sendInitMessage();
    }
    
    public void run(){
        while(!this.socket.isClosed()){
            this.receiveMessagesRoutine();
        }
    }

    
    @Override
    protected void handleInitializeMessage(Message message) {
        System.out.println("Unimplemented method 'handleInitializeMessage'");
    }

    @Override
    protected void handleHeartbeatMessage(Message message) {
        System.out.println("Unimplemented method 'handleHeartbeatMessage'");
    }

    @Override
    protected void handleSyncNodeListMessage(Message message) {
        System.out.println("Unimplemented method 'handleSyncNodeListMessage'");
    }

    @Override
    protected void handleNavigationMessage(Message message) {
        System.out.println("Unimplemented method 'handleNavigationMessage'");
    }

    @Override
    protected void handleSuccessMessage(Message message) {
        this.lastAnswer = message.getPayload();
        System.out.println("Saved last message");
    }

    @Override
    protected void handleErrorMessage(Message message) {
        System.out.println("Unimplemented method 'handleNavigationMessage'");
    }

    @Override
    protected void handleAckMessage(Message message) {
        System.out.println("Unimplemented method 'handleNavigationMessage'");
    }

    private void sendInitMessage(){
        System.out.println(this.ip + " found leader socket");
        InetSocketAddress payload = new InetSocketAddress(this.ip, this.port);
        Message message = new Message(this.ip, this.parentClient.getEntryPointIp(), payload, MessageType.INITIALIZE);
        Message response = this.sendMessageGetResponse(message);
        
        if(response.getType() == MessageType.SUCCESS){
            System.out.println(this.ip + " received initial leader response: " + response.getPayload() + ". Connection established");
        }
        else{
            System.out.println("Init Message from " + this.ip + " was not answered with Success.");
        }            
    }

    public Object getLastAnswer() {
        return this.lastAnswer;
    }
    public void setLastAnswer(Object lastAnswer) {this.lastAnswer = lastAnswer;}
}
