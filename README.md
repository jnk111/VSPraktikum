# VS - Praktikum

Das Eclipse - Project für das Praktikum

### Aufgabe 1
#### Dice-Service
#### -----------------
##### Wuerfeln
* ```$ curl -i http://localhost:4567/dice;echo ```

<br />
##### Wuerfeln mit Spieler und Spielinfo
* ```$ curl -i -H "Content-Type: application/json" http://localhost:4567/dice?player=http://localhost:4567/users/mario&game=http://somegameuri:4567/gameid; echo ```

<br />
#### User-Service
#### -----------------
##### User anlegen
* ```$ curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d "{'id':'/users/mario','name':'Mario', 'uri':'http://localhost:4567/users/mario'}" http://localhost:4567/users ```

<br />
##### User updaten
* ```$ curl -i -X PUT 'http://localhost:4567/users/mario?uri=/hello&name=Wario';echo ```

<br />
##### User loeschen
* ``` $ curl -i -X DELETE http://localhost:4567/users/mario;echo ```

<br />
##### Spezifischen User ermitteln
* ``` $ curl -i -H "Content-Type: application/json" http://localhost:4567/users/mario; echo ```

<br />
##### Userlist
* ``` $ curl -i -H "Content-Type: application/json" http://localhost:4567/users; echo ```

<br />

## TODO:
* Yellowpages
** Dice-Service JSON: ``` {'name':'Jan, Malte','description':'Rolls the Dice', 'service':'DiceService', uri':'http://172.18.0.35:4567/dice'} ```
** User-Service JSON: ``` {'name':'Jan, Malte','description':'User Service', 'service':'UserService', uri':'http://172.18.0.36:4567/users'} ```

### Aufgabe 2
### . . .





