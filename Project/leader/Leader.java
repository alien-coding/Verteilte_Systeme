package Project.leader;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Project.Node;
import Project.NodeSaver;
import Project.Role;
import Project.message.Message;
import Project.message.MessageType;


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
        this.sendMessageToAll(this.parentNode.getAllKnownNodes(), MessageType.SYNC_NODE_LIST); 
    }  

    private void sendMessageToAll(Object payload, MessageType type){
        for (LeaderMessageHandler connection : this.connections) {
            Message toSend = new Message(this.parentNode.getIp(), "test", payload, type);
            connection.sendMessage(toSend);
        }
    }

    public LinkedList<LeaderMessageHandler> getConnections(){return this.connections;}
    public void setConnections(LinkedList<LeaderMessageHandler> connections){this.connections = connections;}
    public Node getParentNode(){return this.parentNode;}
}
