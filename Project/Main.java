package project;

import project.client.Client;
import project.helpers.Coordinate;

public class Main {
    public static void main(String[] args) {
        Node node1 = new Node(Role.LEADER, "127.0.0.1", 200);
        node1.start();

        Node node2 = new Node(Role.FOLLOWER, "127.0.0.2", 200);
        node2.setLeader("127.0.0.1", 200);

        Node node3 = new Node(Role.FOLLOWER, "127.0.0.3", 200);
        node3.setLeader("127.0.0.1", 200);

        node2.start();
        // Util.sleep(500);
        node3.start();
        Util.sleep(5000);

        Client client1 = new Client("127.0.1.1", 200, new Coordinate((short) 1, (short) 1), new Coordinate((short) 99, (short) 99));
        client1.setEntryPointIp("127.0.0.2");
        client1.setEntryPointPort(200);
        client1.start();

        // Node node4 = new Node(Role.FOLLOWER, "127.0.0.4", 200);
        // node4.setLeader("127.0.0.1", 200);
        // node4.start();

        // Util.sleep(5000);

        // node3.setRole(Role.UNKNOWN);
        // Util.sleep(1000);
        // node2.setRole(Role.UNKNOWN);
        // Util.sleep(1);

        // node1.setRole(Role.FOLLOWER);
        // System.out.println("changed role to" + node1.getRole());
    }
}
