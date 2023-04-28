package Project.follower;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Project.Node;
import Project.message.Message;
import Project.message.MessageType;

public class Follower extends Thread {
    private Node parentNode;
    private String leaderIp;
    private int leaderPort;

    private FollowerLeaderMessageHandler connectionToLeader;

    private LinkedList<Socket> connections = new LinkedList<Socket>(); //all accepted connections are added here
    
    public Follower(Node parentNode, String leaderIp, int leaderPort){
        this.parentNode = parentNode;
        this.leaderIp = leaderIp;
        this.leaderPort = leaderPort;
    }

    public void run(){
        //TODO: handle heartbeats
        this.initLeaderConnection();
        try {
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(this.parentNode.getIp(), this.parentNode.getPort());
            serverSocket.bind(address);
            while(!serverSocket.isClosed()){
                Socket newConnection = serverSocket.accept();
                FollowerClientMessageHandler messageHandler = new FollowerClientMessageHandler(this, parentNode, newConnection);
                this.connections.add(newConnection);
                messageHandler.start();
            }
            serverSocket.close();
            
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private void initLeaderConnection(){
        try {
            Socket leaderSocket = new Socket(this.leaderIp, this.leaderPort);
            this.connectionToLeader = new FollowerLeaderMessageHandler(parentNode, leaderSocket);
            // this.connections.add(messageHandler);

            System.out.println(this.parentNode.getIp() + " connected successfully to leader node");
            InetSocketAddress payload = new InetSocketAddress(this.parentNode.getIp(), this.parentNode.getPort());
            Message message = new Message(this.parentNode.getIp(), this.parentNode.leader_ip, payload, MessageType.INITIALIZE);
            Message response = this.connectionToLeader.sendMessageGetResponse(message);
            System.out.println(this.parentNode.getIp() + " received leader response: " + response.getPayload());

            //only start when register answer is received, after it the connection starts to listen for heartbeats etc.
            this.connectionToLeader.start();
        } catch (IOException e) {
            System.out.println("Connecting to leader failed, self is " + this.parentNode.getIp());
            System.err.println(e.toString());
        }
    }

    public FollowerLeaderMessageHandler getConnectionToLeader() {return this.connectionToLeader;}
    public void setConnectionToLeader(FollowerLeaderMessageHandler connectionToLeader) {this.connectionToLeader = connectionToLeader;}
}
