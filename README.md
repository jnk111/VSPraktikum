# VS - Praktikum

Das Eclipse - Project für das Praktikum

### Aufgabe 1
#### Dice-Service
#### -----------------
##### Wuerfeln
* ```$ curl -i http://localhost:4567/dice;echo ```

<br />
##### Wuerfeln mit Spieler und Spielinfo
* ```$ curl -i -H "Content-Type: application/json" http://localhost:4567/dice2?name=http://localhost:4567/users/mario; echo ```

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
* Yellopages

### Aufgabe 2
### . . .





