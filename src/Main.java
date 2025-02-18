import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String mode = null;
        int port = 0;
        Path path = null;
        String hostName = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode" -> mode = args[++i];
                case "-port" -> port = Integer.parseInt(args[++i]);
                case "-map" -> path = Paths.get(args[++i]);
                case "-host" -> hostName = args[++i];
            }
        }

        if (!mode.equals("server") && !mode.equals("client")) {
            throw new IllegalArgumentException("Invalid mode. Use 'server' or 'client'.");
        }
        if (port < 1024 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number. Use a value between 1024 and 65535.");
        }
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Map file not found: " + path);
        }

        if(mode.equals("server")) {
            GameServer gameServer = new GameServer(port, path);
            gameServer.start();
        } else {
            GameClient gameClient = new GameClient(hostName, port, path);
            gameClient.start();
        }
    }
}
