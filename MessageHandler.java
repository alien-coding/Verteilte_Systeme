import java.io.*;
import java.net.Socket;

import Message.*;

public abstract class MessageHandler extends Thread{
    protected ObjectInputStream inputStream;
    protected ObjectOutputStream outputStream;
    protected Socket socket;
    protected Node parentNode;

    public MessageHandler(Node parentNode, Socket newConnection){
        this.parentNode = parentNode;
        this.socket = newConnection;
        this.initializeStreams(newConnection);
    }

    protected Message readMessage(){
        try {
            return (Message) this.inputStream.readObject();
        } catch (Exception e){
            System.err.println(e.toString());
            return null;
        }
    }

    public Message sendMessageGetResponse(Message message){
        try {
            this.outputStream.writeObject(message);
            // this.outputStream.flush();
            Message response = this.readMessage();
            return response;

        } catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }
    }

    public void sendMessage(Message message){
        try {
            this.outputStream.writeObject(message);
        
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    protected void initializeStreams(Socket newConnection){
        try{
            OutputStream outputStream = newConnection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            this.outputStream = new ObjectOutputStream(dataOutputStream);

            InputStream inputStream = newConnection.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            this.inputStream = new ObjectInputStream(dataInputStream);
        }
        catch(IOException e) {
            System.out.println("Node read initialize failed");
        }        
    }

}
