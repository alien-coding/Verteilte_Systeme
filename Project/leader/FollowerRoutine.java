package project.leader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class FollowerRoutine extends Thread {
    private Leader parentLeader;


    public FollowerRoutine(Leader leader){
        this.parentLeader = leader;
    }

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(this.parentLeader.getParentNode().getIp(), this.parentLeader.getParentNode().getPort());
            serverSocket.bind(address);
            while(!serverSocket.isClosed()){
                Socket newConnection = serverSocket.accept();
                LeaderFollowerMessageHandler messageHandler = new LeaderFollowerMessageHandler(this.parentLeader.getParentNode(), newConnection, this.parentLeader);

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
}
