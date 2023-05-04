package project;

import project.client.Client;
import project.helpers.Coordinate;

public class Main {
    public static void main(String[] args) {
        String ipAddress = "";
        int port = -1;
        if(args[1].contains(":")){
            try {
                String[] netAddress = args[1].split(":");
                ipAddress = netAddress[0];
                port = Integer.parseInt(netAddress[1]);
            } catch (Exception e) {

            }
            System.out.println("ip: " + ipAddress + " port: " + port);
        }
        if(args[0].equalsIgnoreCase("node") || args[0].equalsIgnoreCase("n")){
            if(args.length > 2 && args[2].contains(":")){
                String[] leaderAddress = args[2].split(":");
                String leaderIpAddress = leaderAddress[0];
                int leaderPort = Integer.parseInt(leaderAddress[1]);

                Node node = new Node(Role.FOLLOWER, ipAddress, port);
                node.setLeader(leaderIpAddress, leaderPort);
                System.out.println("ip: " + leaderIpAddress + " port: " + leaderPort);
                node.start();
            }
            else{
                Node node = new Node(Role.LEADER, ipAddress, port);
                node.start();
                System.out.println("leader started");
            }
            
        }
        else if(args[0].equalsIgnoreCase("client") || args[0].equalsIgnoreCase("c")){
            if(args.length == 5){
                if(args[2].contains(":") && args[2].length() >= 9 ){ //9 is minimal ip + port length: 1.1.1.1:1
                    if(args[3].contains(":") && args[4].contains(":")){
                        String[] entrypointAddress = args[2].split(":");
                        String entrypointIpAddress = entrypointAddress[0];
                        int entrypointPort = Integer.parseInt(entrypointAddress[1]);

                        String[] start = args[3].split(":");
                        Coordinate startPoint = new Coordinate(Short.parseShort(start[0]), Short.parseShort(start[1]));

                        String[] destination = args[4].split(":");
                        Coordinate destinationPoint = new Coordinate(Short.parseShort(destination[0]), Short.parseShort(destination[1]));

                        Client client = new Client(ipAddress, port, startPoint, destinationPoint);
                        client.setEntryPointIp(entrypointIpAddress);
                        client.setEntryPointPort(entrypointPort);
                        client.start();
                    }
                }
            }
        }


        // Node node1 = new Node(Role.LEADER, "127.0.0.1", 200);
        // node1.start();
        // Util.sleep(500);

        // Node node2 = new Node(Role.FOLLOWER, "127.0.0.2", 200);
        // node2.setLeader("127.0.0.1", 200);

        // Node node3 = new Node(Role.FOLLOWER, "127.0.0.3", 200);
        // node3.setLeader("127.0.0.1", 200);

        // node2.start();
        // // Util.sleep(500);
        // node3.start();
        // Util.sleep(5000);

        // Client client1 = new Client("127.0.1.1", 200, new Coordinate((short) 1, (short) 1), new Coordinate((short) 50, (short) 50));
        // client1.setEntryPointIp("127.0.0.2");
        // client1.setEntryPointPort(200);

        // Client client2 = new Client("127.0.1.2", 200, new Coordinate((short) 50, (short) 50), new Coordinate((short) 1, (short) 1));
        // client2.setEntryPointIp("127.0.0.1");
        // client2.setEntryPointPort(201);

        // client1.start();
        // // Util.sleep(10);
        // client2.start();

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
