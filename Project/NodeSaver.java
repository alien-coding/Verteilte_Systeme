package project;

import java.io.Serializable;

public class NodeSaver implements Serializable {
    private Role role;
    private String ip;
    private int port;

    public NodeSaver(Role role, String ip, int port) {
        this.role = role;
        this.ip = ip;
        this.port = port;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }


}
