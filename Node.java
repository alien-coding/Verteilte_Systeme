import java.util.HashMap;

public class Node extends Thread{
    private Role role = Role.UNKNOWN;
    private String ip;
    private int port;

    private HashMap<String, NodeSaver> allKnownNodes = new HashMap<>();

   

    public Node(Role role, String ip, int port){
        this.role = role;
        this.ip = ip;
        this.port = port;
    }

    public void run(){
        if(this.role == Role.FOLLOWER){
            this.run_follower();
        }
        else if (this.role == Role.LEADER){
            this.run_leader();
        }
    }

    public void run_follower(){

    }

    public void run_leader(){

    }

    public String send_message(){
        return "passt";
    }

    public HashMap<String, NodeSaver> getAllKnownNodes (){return this.allKnownNodes;}
    public void setAllKnownNodes(HashMap<String,NodeSaver> knownNodes){this.allKnownNodes = knownNodes;}
}
