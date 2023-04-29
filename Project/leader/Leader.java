package Project.leader;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Project.HandleAllNodesSync;
import Project.Node;
import Project.NodeSaver;
import Project.Role;


public class Leader extends Thread{
    private Node parentNode;
    private LinkedList<LeaderMessageHandler> connections = new LinkedList<LeaderMessageHandler>(); //all accepted connections are added here

    public Leader(Node node){
        this.parentNode = node;
    }

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(this.parentNode.getIp(), this.parentNode.getPort());
            serverSocket.bind(address);
            while(!serverSocket.isClosed()){
                Socket newConnection = serverSocket.accept();
                LeaderMessageHandler messageHandler = new LeaderMessageHandler(parentNode, newConnection, this);
                messageHandler.start();
                // this.updatedNodeList(messageHandler);
            }
            serverSocket.close();
        }
        catch (IOException e){
            System.out.println("Opening as a leader failed");
            System.err.println(e.toString());
        }
    }

    public void updatedNodeList(LeaderMessageHandler messageHandler){
        this.connections.add(messageHandler);
        NodeSaver newFollower = new NodeSaver(Role.FOLLOWER, messageHandler.getFollowerIp(), messageHandler.getFollowerPort());
        this.parentNode.getAllKnownNodes().put(messageHandler.getFollowerIp(), newFollower);
        
        HandleAllNodesSync handleAllNodesSync = new HandleAllNodesSync(this);
        handleAllNodesSync.start();
    }

    public LinkedList<LeaderMessageHandler> getConnections(){return this.connections;}
    public void setConnections(LinkedList<LeaderMessageHandler> connections){this.connections = connections;}
    public Node getParentNode(){return this.parentNode;}
}
