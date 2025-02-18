import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class GameClient extends GameNode{
    public GameClient(String hostName, int port, Path path) {
        super(path);
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            gameMap.printMap();
            this.in = in;
            this.out = out;
            lastSentMessage = getStartMessage();
            socket.setSoTimeout(TIMEOUT);
            loop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStartMessage() throws IOException {
        String field = fieldChooser.chooseField();
        return sendMessage("start", field);
    }
}
