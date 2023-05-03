# Node

    -run_follower: wenn aufgerufen, dann wurde Protokoll für Leader finden abgeschlossen, es gibt einen Leader und die Node selbst ist es nicht.

## Heatbeat timeouts

## bei Leader

-wenn ein follower nicht mehr antwortet wird davon ausgegangen, das er tot ist. (gilt auch für mehrere)
->direkt nach EINER fehlenden Antwort (timeout von 2 sek)
--> connection schließen (alles was dazugehört wie clients benachrichtigen)

## bei Follower

-wenn leader keine heartbeats mehr sendet (timeout von 2 sek)
-frage alle anderen Nodes an, ob diese noch leben
    -wenn ja -> neue Election
    -wenn nein -> selber in Masterposition, da er nun alleine im Netz ist

## Election

-Wenn nur eine node exisitert: geht direkt in Leader rolle, wartet auf follower (damit system auch mit nur einer Node arbeitet)
-Wenn leader schon existiert (es gibt mehr als eine Node):
    -Als follower dem bestehenden leader beitreten in Netz
-Wenn leader nicht mehr antwortet (tot):
    -Neubestimmung des Leaders: geringste IP wird neuer Leader.
    ->Dazu: nodes fragen alle anderen an, welche niedrigste sie kennen. Wenn nein -> Leader, wenn ja -> Weiterleitung

## MessageTypes

### Net

-INITIALIZE: New Node wants to connect and communicate with Node
-HEARTBEAT: Sending a heartbeat, waiting for an ACK
-SYNC_NODE_LIST: Leader to follower, this means there is a new node list in the body. Else, this is a request with "GET" in the Body.

### Client

-NAVIGATION: Sending position, waiting for SUCCESS with next position
-_INITIALIZE_: same as in Net just with Client to Node

### Answers

-SUCCESS: Every request that has not failed
-ERROR: If request cannot be handled, Error is returned. Body contains more information
-ACK: Heartbeat was acknowledged. Special answer to not interfere with other requests that are answered with SUCCESS

## TODOs

Client connections to anywhere (relativ fix)
Dynamic leader searching
Functionality of navigating
    + save navigation stuff persistent
Testen was ohne init message mit leader passiert
