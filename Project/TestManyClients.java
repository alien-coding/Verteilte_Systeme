package project;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

import project.client.Client;
import project.helpers.Coordinate;

public class TestManyClients {
    /**
     * Uses three nodes: 
     * Leader with 127.0.0.1:201 as navigation port
     * Follower 1 with 127.0.0.2:201 
     * Follower 2 with 127.0.0.3:201 
     * @param args not used, may be null
     */
    public static void main(String[] args) {
        LinkedList<Client> allClients = new LinkedList<>();

        int howManyClients = 3;

        Instant start = Instant.now();
        
        for (int i = 0; i < howManyClients; i++) {
            String ownIpAddress = "127.0.1." + (i+1);
            Client client = new Client(ownIpAddress, 200, new Coordinate((short) 1, (short) (10+i)), new Coordinate((short) 50, (short) (5+i)));
            String entryPoint = "127.0.0." + (i%3+1);

            client.setEntryPointIp(entryPoint);
            client.setEntryPointPort(201);
            allClients.add(i, client);
            client.start();
            Util.sleep(30);
        }
                
        while(!allClients.getLast().getPosition().compare(allClients.getLast().getDestination())){
            Util.sleep(50);
        }

        Instant end = Instant.now();
        System.out.println(".........................Time needed (in ms): " + Duration.between(start, end).toMillis());
    }
}
