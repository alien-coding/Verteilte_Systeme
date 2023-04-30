package project.client;

import java.io.IOException;
import java.net.Socket;

import project.message.Message;
import project.message.MessageType;

public class Client extends Thread{
    private String ip;
    private int port;

    private String entryPointIp;
    private int entryPointPort;

    private ClientNodeMessageHandler messageHandler;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void run(){
        try {
            Socket entryPointSocket = new Socket(this.entryPointIp, this.entryPointPort); 
            this.messageHandler = new ClientNodeMessageHandler(entryPointSocket, this.ip, this.port, this);
            this.messageHandler.start();
            this.messageHandler.sendMessage(new Message("", "", "test", MessageType.NAVIGATION));
            
        } catch (IOException e) {
            System.out.println(this.ip + ": connecting to leader failed");
            System.err.println(e.toString());
        }
    }
    
    public String getIp() {return this.ip;}
    public void setIp(String ip) {this.ip = ip;}
    public int getPort() {return this.port;}
    public void setPort(int port) {this.port = port;}
    public String getEntryPointIp() {return this.entryPointIp;}
    public void setEntryPointIp(String entryPointIp) {this.entryPointIp = entryPointIp;}
    public int getEntryPointPort() {return this.entryPointPort;}
    public void setEntryPointPort(int entryPointPort) {this.entryPointPort = entryPointPort;}
    public ClientNodeMessageHandler getMessageHandler() {return this.messageHandler;}
    public void setMessageHandler(ClientNodeMessageHandler messageHandler) {this.messageHandler = messageHandler;};
}
