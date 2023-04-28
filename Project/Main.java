package Project;
public class Main {
    public static void main(String[] args) {
        Node node1 = new Node(Role.LEADER, "127.0.0.1", 200);
        node1.start();
        Node node2 = new Node(Role.FOLLOWER, "127.0.0.2", 200);
        node2.leader_ip = "127.0.0.1";
        node2.leader_port = 200;
        node2.start();

        Node node3 = new Node(Role.FOLLOWER, "127.0.0.3", 200);
        node3.leader_ip = "127.0.0.1";
        node3.leader_port = 200;
        node3.start();


        // Util.sleep(1);

        // node1.setRole(Role.FOLLOWER);
        // System.out.println("changed role to" + node1.getRole());
    }
}
