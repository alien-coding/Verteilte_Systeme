package project.message;

import java.io.*;
import java.net.Socket;

import project.Node;

public abstract class MessageHandler extends Thread{
    protected ObjectInputStream inputStream;
    protected ObjectOutputStream outputStream;
    protected Socket socket;
    protected Node parentNode;
    protected String ip;
    protected int port;

    public MessageHandler(Node parentNode, Socket newConnection){
        this.parentNode = parentNode;
        this.socket = newConnection;
        this.ip = parentNode.getIp();
        this.port = parentNode.getPort();
        this.initializeStreams(newConnection);
    }

    public MessageHandler(Socket newConnection, String ipAddress, int port){
        this.parentNode = null;
        this.socket = newConnection;
        this.ip = ipAddress;
        this.port = port;
        this.initializeStreams(newConnection);
    }

    protected Message readMessage() {
        try {
            Message received = (Message) this.inputStream.readObject();
            return received;
        } catch (EOFException e) {
            System.err.println(e.toString());
            this.closeSocket();
            return null;
        } catch (Exception e){
            System.err.println(e.toString());
            return null;
        }
    }

    public Message sendMessageGetResponse(Message message){
        try {
            this.outputStream.writeObject(message);
            Message response = this.readMessage();
            return response;
        } catch (EOFException e) {
            this.closeSocket();
            return null;
        } catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }
    }

    public void sendMessage(Message message){
        try {
            this.outputStream.writeObject(message);
        } catch (EOFException e) {
            this.closeSocket();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    protected void receiveMessagesRoutine(){
        try {
            Message message = this.readMessage();
            System.out.println(this.ip + " received a " + message.getType().toString() + " message: " + message.getPayload());
            switch (message.getType()) {
                case INITIALIZE:
                    this.handleInitializeMessage(message);
                    break;
                case HEARTBEAT:
                    this.handleHeartbeatMessage(message);
                    break;
                case SYNC_NODE_LIST:
                    this.handleSyncNodeListMessage(message);
                    break;   
                case NAVIGATION:
                    this.handleNavigationMessage(message);
                    break;
                case SUCCESS:
                    this.handleSuccessMessage(message);
                    break;
                case ERROR:
                    this.handleErrorMessage(message);
                    break;
                case ACK:
                    this.handleAckMessage(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // System.err.println(e.toString());
        }
    }

    protected abstract void handleInitializeMessage(Message message);
    protected abstract void handleHeartbeatMessage(Message message);
    protected abstract void handleSyncNodeListMessage(Message message);
    protected abstract void handleNavigationMessage(Message message);

    //the following three codes should not be sent without context (proactively), so they should only received by a routine
    //that is waiting for an answer (when using sendMessageGetResponse), not by the receivingMessages Routine

    protected void handleSuccessMessage(Message message){
        Message answer = new Message(this.ip, message.getSender(), "Please do not send answer codes as request.", MessageType.ERROR);
        this.sendMessage(answer);
    }

    protected void handleErrorMessage(Message message){
        Message answer = new Message(this.ip, message.getSender(), "Please do not send answer codes as request.", MessageType.ERROR);
        this.sendMessage(answer);
    }

    protected void handleAckMessage(Message message){
        Message answer = new Message(this.ip, message.getSender(), "Please do not send answer codes as request.", MessageType.ERROR);
        this.sendMessage(answer);
    }


    protected void initializeStreams(Socket newConnection){
        try{
            OutputStream outputStream = newConnection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            this.outputStream = new ObjectOutputStream(dataOutputStream);

            InputStream inputStream = newConnection.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            this.inputStream = new ObjectInputStream(dataInputStream);
        }
        catch(IOException e) {
            System.out.println("Node read initialize failed");
        }        
    }

    protected void closeSocket(){
        try {
            System.out.println(this.ip + " lost connection to opponent, closing own socket");
            this.socket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public Socket getSocket(){return this.socket;}
    public Node getParentNode(){return this.parentNode;}
}
