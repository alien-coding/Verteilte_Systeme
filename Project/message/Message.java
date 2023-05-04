package project.message;
import java.io.Serializable;
import java.time.Instant;

/**
 * Used for communication. Every sent message must be from type message.
 */
public class Message implements Serializable {
    private String sender;
    private String receiver;
    private Object payload;
    private Instant time = Instant.now();
    private MessageType type;
    
    /**
     * Checks message creation for null values.
     * @param sender own ip address so message can be answered
     * @param receiver destination ip address 
     * @param payload content of message, objects class must be Serializable 
     * @param type Enum of MessageType, used for processing the message
     * @throws IllegalArgumentException when one of the arguments is null
     */
    public Message(String sender, String receiver, Object payload, MessageType type)
    {
        if(sender != null && receiver != null && payload != null && type != null){
            this.sender = sender;
            this.receiver = receiver;
            this.payload = payload;
            this.type = type;
        }
        else{
            throw new IllegalArgumentException("Arguments must not be null.");
        }
        
    }

    public Object getPayload (){return this.payload;}
    public void setPayload (Object payload){this.payload = payload;}
    public Instant getTime (){return this.time;}
    public void setTime (Instant time){this.time = time;}
    public MessageType getType (){return this.type;}
    public void setType (MessageType type){this.type = type;}
    public String getSender(){return sender;}
    public void setSender(String sender){this.sender = sender;}
    public String getReceiver (){return this.receiver;}
    public void setReceiver(String receiver){this.receiver = receiver;}
}
