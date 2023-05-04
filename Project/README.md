# Verteilte_Systeme

For running, please compile code and then call main.java with:

Node: java project.Main node (ipAdresse:port) (remoteAdresse)*
Client: java project.Main client (ipAdresse:port) (remoteAdresse) (startCoordinate) (destinationCoordinate)
'*' indicates non required arguments.

Usage examples are:
„java project.Main node 127.0.0.1:200“  --> Leader 
„java project.Main node 127.0.0.2:200 127.0.0.1:200“  --> Follower
„java project.Main node 127.0.0.3:200 127.0.0.1:200“ --> Follower
„java project.Main node client 127.0.1.1:200 127.0.0.1:201 1:1 10:10“

For testing the system with a high number of clients, please initialize a leader and two followers.
They should have the IP addresses 127.0.0.1-3 (just like in the example above). Then, with a fourth process, start project.TestManyClients.
It will create as many clients as specified and let them connect with all three nodes. Then, these test clients will simulate a navigation.
Please note that automatic time measuring only works when the number of initialized is x % 3 = 1.
