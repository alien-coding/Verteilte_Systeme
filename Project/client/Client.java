package project.client;

import java.io.IOException;
import java.net.Socket;

import project.Util;
import project.helpers.Coordinate;
import project.message.Message;
import project.message.MessageType;

public class Client extends Thread{
    private String ip;
    private int port;

    private String entryPointIp;
    private int entryPointPort;

    private Coordinate destination;
    private Coordinate position;

    private ClientNodeMessageHandler messageHandler;

    public Client(String ip, int port, Coordinate start, Coordinate destination){
        this.ip = ip;
        this.port = port;
        this.destination = destination;
        this.position = start;
    }

    public void run(){
        try {
            Socket entryPointSocket = new Socket(this.entryPointIp, this.entryPointPort); 
            this.messageHandler = new ClientNodeMessageHandler(entryPointSocket, this.ip, this.port, this);
            this.messageHandler.start();
            
        } catch (IOException e) {
            System.out.println(this.ip + ": connecting to leader failed");
            System.err.println(e.toString());
        }
    }

    public void startNavigation(){
        while((this.destination.getX() != this.position.getX()) && (this.destination.getY() != this.position.getY())){
            Coordinate[] payload = new Coordinate[2];
            payload[0] = this.position;
            payload[1] = this.destination;

            this.messageHandler.sendMessage(new Message(this.ip, this.entryPointIp, payload, MessageType.NAVIGATION));
            while(this.messageHandler.getLastAnswer() == null){
                Util.sleep(1);
            }
            Coordinate nextStep = (Coordinate) this.messageHandler.getLastAnswer();
            this.messageHandler.setLastAnswer(null);
            System.out.println("Next Step: x: " + nextStep.getX() + " y: " + nextStep.getY());
            this.position = nextStep;
            // Util.sleep(10);
        }
        try {
            this.messageHandler.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
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
