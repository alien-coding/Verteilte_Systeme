package Project.leader;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import Project.Node;
import Project.NodeSaver;
import Project.Role;
import Project.Util;
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

                Boolean isRegistered = messageHandler.registerClient();
                if(isRegistered){
                    messageHandler.start();
                    NodeSaver newFollower = new NodeSaver(Role.FOLLOWER, messageHandler.getFollowerIp(), messageHandler.getFollowerPort());
                    this.parentNode.addToAllKnownNodes(messageHandler.getFollowerIp(), newFollower);
                    this.connections.add(messageHandler);
                    System.out.println("Leader added new connection to all known nodes: " + this.parentNode.getAllKnownNodes());
                    
                    Message message = new Message(this.parentNode.getIp(), this.connections.getLast().getFollowerIp(), this.parentNode.getAllKnownNodes().clone(), MessageType.SYNC_NODE_LIST);
                    Util.sleep(1);
                    for (LeaderMessageHandler connection : connections) {
                        connection.sendMessage(message);
                    }
                }

                
                // this.updatedNodeList(messageHandler);
            }
            serverSocket.close();
        }
        catch (IOException e){
            System.out.println("Opening as a leader failed");
            System.err.println(e.toString());
        }
    }

    public LinkedList<LeaderMessageHandler> getConnections(){return this.connections;}
    public void setConnections(LinkedList<LeaderMessageHandler> connections){this.connections = connections;}
    public Node getParentNode(){return this.parentNode;}
}
