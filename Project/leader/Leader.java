package project.leader;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import project.Node;
import project.Util;
import project.message.Message;
import project.message.MessageType;


public class Leader extends Thread{
    private Node parentNode;
    private LinkedList<LeaderMessageHandler> nodeConnections = new LinkedList<LeaderMessageHandler>(); //all accepted connections are added here

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

                Boolean isRegistered = messageHandler.registerConnection(); //wait for init from new Node or follower
                if(isRegistered){
                    messageHandler.start();
                }
            }
            serverSocket.close();
        }
        catch (IOException e){
            System.out.println("Opening as a leader failed");
            System.err.println(e.toString());
        }
    }

    public void updateNodeList(){
        Message message = new Message(this.parentNode.getIp(), "", this.parentNode.getAllKnownNodes().clone(), MessageType.SYNC_NODE_LIST);
        Util.sleep(10);  //so sending message does not happen in exact same time as first heartbeat (triggered by messageHandler.start)
        for (LeaderMessageHandler connection : nodeConnections) {
            message.setReceiver(connection.getFollowerIp());
            connection.sendMessage(message);
        }
    }

    public LinkedList<LeaderMessageHandler> getNodeConnections(){return this.nodeConnections;}
    public void setNodeConnections(LinkedList<LeaderMessageHandler> connections){this.nodeConnections = connections;}
    public Node getParentNode(){return this.parentNode;}
}
