package project;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

import project.client.Client;
import project.helpers.Coordinate;

public class TestManyClients {
    public static void main(String[] args) {
        LinkedList<Client> allClients = new LinkedList<>();

        Instant start = Instant.now();
        
        for (int i = 0; i < 52; i++) {
            String ownIpAddress = "127.0.1." + (i+1);
            Client client = new Client(ownIpAddress, 200, new Coordinate((short) 1, (short) (10+i)), new Coordinate((short) 50, (short) (5+i)));
            String entryPoint = "127.0.0." + (i%3+1);
            // System.out.println(entryPoint + " <- synth stuff");

            client.setEntryPointIp(entryPoint);
            client.setEntryPointPort(201);
            allClients.add(i, client);
            client.start();
            Util.sleep(30);
        }
                
        // for (Client client : allClients) {
        //     client.startNavigation();
        // }
        while(!allClients.getLast().getPosition().compare(allClients.getLast().getDestination())){
            Util.sleep(50);
        }
        // node1.getArea();
        Instant end = Instant.now();
        System.out.println(".........................Time needed (in s): " + Duration.between(start, end).toMillis());
        // node1.setRole(Role.UNKNOWN);
        // node3.setRole(Role.UNKNOWN);
        // node2.setRole(Role.UNKNOWN);
    }
}
