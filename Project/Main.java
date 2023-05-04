package project;

import project.client.Client;
import project.helpers.Coordinate;

public class Main {
    /**
     * Gets Arguments for initializing either a Node (leader or follower) or a client. For detailed usage, see README.md
     * @param args
     */
    public static void main(String[] args) {
        String ipAddress = "";
        int port = -1;
        try {
            if(args[1].contains(":")){
                try {
                    String[] netAddress = args[1].split(":");
                    ipAddress = netAddress[0];
                    port = Integer.parseInt(netAddress[1]);
                } catch (Exception e) {
                    System.err.println(e.toString());
                    printUsage();
                }
            }
            else{
                printUsage();
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
                        else{
                            printUsage();
                        }
                    }
                    else{
                        printUsage();
                    }
                }
                else{
                    printUsage();
                }
            }
            else{
                printUsage();
            }
        } catch (Exception e) {
            printUsage();
        }
    }
    
    private static void printUsage(){
        System.out.println("Usage: project.Main (device type) (ipAddress:port) (leaderIp / entrypointIp)* (startPosition)* (destination)*");
        System.out.println("main.java node 127.0.0.1:200 --> (leader)");
        System.out.println("main.java node 127.0.0.2:200 127.0.0.1:201 --> (follower)");
        System.out.println("main.java client 127.0.1.1:200 127.0.0.1:200 1:1 10:10 --> (client)");
    }
}
