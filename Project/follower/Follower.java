package project.follower;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import project.Node;
import project.message.Message;
import project.message.MessageType;

public class Follower extends Thread {
    private Node parentNode;
    private String leaderIp;
    private int leaderPort;

    private FollowerLeaderMessageHandler connectionToLeader;
    private LinkedList<Socket> connections = new LinkedList<Socket>(); //all accepted connections are added here
    private LinkedList<FollowerClientMessageHandler> clientConnections = new LinkedList<FollowerClientMessageHandler>(); //all accepted connections are added here
    
    public Follower(Node parentNode, String leaderIp, int leaderPort){
        this.parentNode = parentNode;
        this.leaderIp = leaderIp;
        this.leaderPort = leaderPort;
    }

    public void run(){
        this.initLeaderConnection();
        try {
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(this.parentNode.getIp(), this.parentNode.getPort());
            serverSocket.bind(address);
            while(!serverSocket.isClosed() && !this.connectionToLeader.getSocket().isClosed()){
                Socket newConnection = serverSocket.accept();
                FollowerClientMessageHandler messageHandler = new FollowerClientMessageHandler(this, parentNode, newConnection);
                this.clientConnections.add(messageHandler);
                this.connections.add(newConnection);
                messageHandler.start();
            }
            serverSocket.close();
            
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Connecting the Follower node to the leader node. At this point, the leader IP has to be known (set in constructor).
     * After connection, a init message with own address has to be sent. When everything is fine (get SUCCESS returned), the 
     * connectionToLeader is started, which contains routine for answering proactive messages. 
     */
    private void initLeaderConnection(){
        try {
            Socket leaderSocket = new Socket(this.leaderIp, this.leaderPort);
            this.connectionToLeader = new FollowerLeaderMessageHandler(this, parentNode, leaderSocket);
            // this.connections.add(messageHandler);

            System.out.println(this.parentNode.getIp() + " found leader socket");
            InetSocketAddress payload = new InetSocketAddress(this.parentNode.getIp(), this.parentNode.getPort());
            
            Message message = new Message(this.parentNode.getIp(), this.parentNode.getLeaderIp(), payload, MessageType.INITIALIZE);
            Message response = this.connectionToLeader.sendMessageGetResponse(message);
            System.out.println(this.parentNode.getIp() + " received initial leader response: " + response.getPayload());

            if(response.getType() == MessageType.SUCCESS){
                this.connectionToLeader.start();
            }
            else{
                throw new IOException("Init Message from " + this.parentNode.getIp() + " was not answered with Success.");
            }            
        } catch (IOException e) {
            System.out.println(this.parentNode.getIp() + ": connecting to leader failed");
            System.err.println(e.toString());
            try {
                this.connectionToLeader.getSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //TODO: quit on this point
        }
    }

    public FollowerLeaderMessageHandler getConnectionToLeader() {return this.connectionToLeader;}
    public void setConnectionToLeader(FollowerLeaderMessageHandler connectionToLeader) {this.connectionToLeader = connectionToLeader;}
    public LinkedList<FollowerClientMessageHandler> getClientConnections() {return this.clientConnections;}
}
