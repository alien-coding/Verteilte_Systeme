package project;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import project.follower.Follower;
import project.helpers.*;
import project.leader.Leader;

public class Node extends Thread{
    private Role role = Role.UNKNOWN;
    private String ip;
    private int port;
    private LocalDateTime lastHeartBeat;
    private TrafficArea area;
    private TrafficControlLogic logic;

    private String pathForBackUp;
    private HashMap<String, NodeSaver> allKnownNodes = new HashMap<String, NodeSaver>();

    //TODO: change way of getting leader 
    private String leaderIp;
    private int leaderPort;
    
    
    public Node(Role role, String ip, int port){
        this.role = role;
        this.ip = ip;
        this.port = port;
        this.allKnownNodes.put(this.ip, new NodeSaver(this.role, this.ip, this.port));
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
                this.figureOutNewLeader();
            }
            Util.sleep(100);
        }
    }


    /** 
     * When run_follower is called, the leader has to already be figured out.
    */
    private void run_follower(){
        Follower follower = new Follower(this, this.leaderIp, this.leaderPort);
        follower.start();
        this.waitForRoleChange(Role.FOLLOWER);
        try {
            follower.getConnectionToLeader().getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        follower.interrupt();
    }

    private void run_leader() {
        Leader leader = new Leader(this, this.ip, 201);
        leader.start();
        this.waitForRoleChange(Role.LEADER);
        leader.interrupt();
    }

    private void waitForRoleChange(Role designatedRole){
        while(this.role == designatedRole){
            Util.sleep(1);
        }
    }

    private void figureOutNewLeader(){
        
    }

    public void setLeader(String leaderIp, int leaderPort){
        this.leaderIp = leaderIp;
        this.leaderPort = leaderPort;
        for(HashMap.Entry<String, NodeSaver> entry : this.allKnownNodes.entrySet()){
            if(entry.getValue().getRole() == Role.LEADER){
                entry.getValue().setRole(Role.FOLLOWER); //TODO wtf mach ich hier
            }
        }
        this.allKnownNodes.put(leaderIp, new NodeSaver(Role.LEADER, leaderIp, leaderPort));
    }

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
    public HashMap<String, NodeSaver> getAllKnownNodes() {return this.allKnownNodes;}
    public void setAllKnownNodes(HashMap<String, NodeSaver> allKnownNodes) {this.allKnownNodes = allKnownNodes;}
    public void addToAllKnownNodes(String key, NodeSaver toAdd){this.allKnownNodes.put(key, toAdd);}
    public String getLeaderIp() {return this.leaderIp;}
    public void setLeaderIp(String leaderIp) {this.leaderIp = leaderIp;}
    public int getLeaderPort() {return this.leaderPort;}
    public void setLeaderPort(int leaderPort) {this.leaderPort = leaderPort;}
    public TrafficArea getArea() {return this.area;}
    public void setArea(TrafficArea area) {this.area = area;}
}
