import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class GameServer extends GameNode{
    private final ServerSocket serverSocket;

    public GameServer(int port, Path path) {
        super(path);
        lastSentMessage = null;
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            socket.setSoTimeout(TIMEOUT);
            gameMap.printMap();
            this.in = in;
            this.out = out;
            loop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
