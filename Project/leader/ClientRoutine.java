package project.leader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Runs the Leader routine of accepting clients.
 * Therefore opens server socket and starts for every connection a LeaderClientMessageHandler.* 
 */
public class ClientRoutine extends Thread {
    private Leader parentLeader;

    public ClientRoutine(Leader leader){
        this.parentLeader = leader;
    }

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(this.parentLeader.getAddressForClients(), this.parentLeader.getPortForClients());
            serverSocket.bind(address);
            System.out.println("Leader accepting Clients now");
            while(!serverSocket.isClosed()){
                Socket newConnection = serverSocket.accept();
                LeaderClientMessageHandler messageHandler = new LeaderClientMessageHandler(this.parentLeader.getParentNode(), newConnection, this.parentLeader);

                Boolean isRegistered = messageHandler.registerConnection(); //wait for init from new client
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
