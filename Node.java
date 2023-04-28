import java.time.LocalDateTime;
import java.util.HashMap;
import helpers.*;

public class Node extends Thread{
    private Role role = Role.UNKNOWN;
    private String ip;
    private int port;
    private LocalDateTime lastHeartBeat;
    private TrafficControlLogic logic;
    private String pathForBackUp;
    private HashMap<String, NodeSaver> allKnownNodes = new HashMap<>();
    //TODO: change way of getting leader 
    public String leader_ip;
    public int leader_port;
    
    public Node(Role role, String ip, int port){
        this.role = role;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run(){
        while(true){
            if(this.role == Role.FOLLOWER){
                this.run_follower();
            }
            else if(this.role == Role.LEADER){
                this.run_leader();
            }
            else if(this.role == Role.UNKNOWN){
                System.out.println("help what to do now???");
            }
        }
    }

    /*
     * When run_follower is called, the leader has to already be figured out.
    */
    private void run_follower(){
        //init connection to leader
        //send initialize Message to Leader
        //open for clients, always accept new clients
        // while (this.role == Role.FOLLOWER && (LocalDateTime.now().toInstant(null).toEpochMilli() - this.lastHeartBeat.toInstant(null).toEpochMilli()) <= this.TIMEOUT){
        // }
        Follower follower = new Follower(this, this.leader_ip, this.leader_port);
        follower.start();
        this.waitForRoleChange(Role.FOLLOWER);
        follower.interrupt();
    }

    private void run_leader() {
        Leader leader = new Leader(this);
        leader.start();
        this.waitForRoleChange(Role.LEADER);
        leader.interrupt();
        //open server socket (in a while)
        //always check for enough followers (when more than half are not responding go in idle state)
        //read messages, answer them
            //whilst doing so, keep data consistent
    }

    private void waitForRoleChange(Role designatedRole){
        while(this.role == designatedRole){
            Util.sleep(1);
        }
    }

    public HashMap<String, NodeSaver> getAllKnownNodes (){return this.allKnownNodes;}
    public void setAllKnownNodes(HashMap<String,NodeSaver> knownNodes){this.allKnownNodes = knownNodes;}


    public Role getRole() {return this.role;}
    public void setRole(Role role) {this.role = role;}
    public String getIp() {return this.ip;}
    public void setIp(String ip) {this.ip = ip;}
    public int getPort() {return this.port;}
    public void setPort(int port) {this.port = port;}
    public LocalDateTime getLastHeartBeat() {return this.lastHeartBeat;}
    public void setLastHeartBeat(LocalDateTime lastHeartBeat) {this.lastHeartBeat = lastHeartBeat;}
    public TrafficControlLogic getLogic() {return this.logic;}
    public void setLogic(TrafficControlLogic logic) {this.logic = logic;}
    public String getPathForBackUp() {return this.pathForBackUp;}
    public void setPathForBackUp(String pathForBackUp) {this.pathForBackUp = pathForBackUp;}
}
