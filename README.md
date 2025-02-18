# Battleship Game

A network-based client-server battleship game implemented in Java.

## Launch Parameters
* `-mode [server|client]` - application mode
* `-port N` - communication port
* `-map map-file` - path to the ship map file
* `-host hostName` - hostname (client mode only)

## Compilation

```bash
javac -d out src/*.java
```

## Example Execution

```bash
java -cp out Main -mode server -port 8000 -map maps/v1.txt
java -cp out Main -mode client -port 8000 -map maps/v2.txt -host localhost
```

## Map
* Ships can have 1, 2, 3, or 4 masts.
* A ship consists of one or more side-by-side fields containing a mast.
* Masts touching only at the corners do not form a ship.

## Communication Protocol
* Communication uses the TCP protocol, UTF-8 encoding.
* Message format: `command;coordinates\n`
* Example commands: `start`, `miss`, `hit`, `hit and sunk`, `last ship sunk`

## Game Flow
* The client automatically initiates the game with the `start` command.
* The server and client automatically take turns shooting at each other, responding with messages according to the state of the board.
* The game ends when one player loses all their ships.

## Error Handling
* In case of an invalid command or no response within 1 second, the last message is resent.
* After three failed attempts, the application terminates with the message `Communication error`.

## Author
**Wojciech Mikula** - project creator