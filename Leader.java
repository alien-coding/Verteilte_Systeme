
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


public class Leader extends Thread{
    private Node parentNode;
    private LinkedList<MessageHandler> connections = new LinkedList<MessageHandler>(); //all accepted connections are added here

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
                this.connections.add(messageHandler);
                messageHandler.start();
            }
            serverSocket.close();
        }
        catch (IOException e){
            System.out.println("Opening as a leader failed");
            System.err.println(e.toString());
        }
    }

    public LinkedList<MessageHandler> getConnections(){return this.connections;}
    public Node getParentNode(){return this.parentNode;}
}
