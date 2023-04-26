import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Node node1 = new Node(Role.LEADER, "127.0.0.1", 200);
        node1.start();
        Node node2 = new Node(Role.FOLLOWER, "127.0.0.2", 200);
        // node2.run();

        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }

        node1.setRole(Role.UNKNOWN);
        System.out.println("changed role to" + node1.getRole());
    }
}
