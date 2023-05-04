package project.client;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

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

    /**
     * Clients represent the users of the navigation system. They have a starting point and a destination.
     * @param ip own ip address of the client
     * @param port own port of the client
     * @param start coordinates where the client starts
     * @param destination coordinates to which the client wants to travel
     */
    public Client(String ip, int port, Coordinate start, Coordinate destination){
        this.ip = ip;
        this.port = port;
        this.destination = destination;
        this.position = start;
    }
    
    /**
     * Executes the client multithreaded. First, the client will initialize itself with the given entry point.
     * After this, it will loop the navigation message send until the client has arrived at the destination.
     */
    public void run(){
        try {
            Socket entryPointSocket = new Socket(this.entryPointIp, this.entryPointPort); 
            this.messageHandler = new ClientNodeMessageHandler(entryPointSocket, this.ip, this.port, this);
            this.messageHandler.start();
            while(this.messageHandler.getIsInited() == false){
                Util.sleep(100);
            }
            Util.sleep(500);
            this.runNavigation();

        } catch (IOException e) {
            System.out.println(this.ip + ": connecting to leader failed");
            System.err.println(e.toString());
        }
    }

    /**
     * Sends request to the already initialized entry point of the system.
     * Repeats until arrival at destination.
     */
    private void runNavigation(){
        Instant start = Instant.now();
        while(!this.destination.compare(this.position)){
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
        }
        Instant end = Instant.now();
        System.out.println(this.ip + " reached its destination in " + Duration.between(start, end) + "s, quit connection");
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
    public Coordinate getPosition() {return this.position;}
    public Coordinate getDestination() {return this.destination;}
}
