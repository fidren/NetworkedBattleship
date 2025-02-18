import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Path;

public abstract class GameNode {
    protected static final int TIMEOUT = 100000;

    protected Socket socket;
    protected GameMap gameMap;
    protected GameMap opponentMap;
    protected int retries;
    protected String lastSentMessage;
    protected FieldChooser fieldChooser;
    protected BufferedReader in;
    protected PrintWriter out;

    protected GameNode(Path path) {
        this.gameMap = GameMap.getMapInstanceFromFile(path);
        this.opponentMap = GameMap.getUnknownMapInstance();
        this.retries = 0;
        fieldChooser = new FieldChooser(ChooserType.RANDOM);
    }

    protected boolean continueGame() throws IOException {
        String response = in.readLine();
        if (retries >= 3) {
            System.out.println("Communication error");
            return false;
        }

        if (response == null || response.isEmpty()) {
            return true;
        }

        Message message = Message.parseMessage(response);
        System.out.println("received: " + response);
        if (message == null) {
            out.println(lastSentMessage);
            retries++;
            return true;
        }
        retries = 0;

        opponentMap.processCommand(message.command(), fieldChooser.getLastSentRow(), fieldChooser.getLastSentCol());
        if (response.startsWith("last ship sunk")) {
            handleWin();
            return false;
        }

        String resultCommand = gameMap.getResult(message.row() - 1, Message.colAsInt(message.col()));
        if (resultCommand.equals("last ship sunk")) {
            handleLoss(resultCommand);
            return false;
        }

        lastSentMessage = sendMessage(resultCommand, fieldChooser.chooseField());

        return true;
    }

    protected String sendMessage(String resultCommand, String field) {
        String sendMessage = resultCommand + ';' + field + '\n';
        out.print(sendMessage);
        out.flush();
        System.out.print("sent: " + sendMessage);
        return sendMessage;
    }

    private void handleLoss(String resultCommand) {
        out.print(new Message(resultCommand, -1, (char) 0));
        out.flush();
        System.out.println("sent: " + resultCommand);
        System.out.print("Lose\n");
        System.out.print("Opponent's map:\n");
        opponentMap.printMap();
        System.out.print("\n");
        System.out.print("Your map:\n");
        gameMap.printMap();
    }

    private void handleWin() {
        System.out.print("Win\n");
        System.out.print("Opponent's map:\n");
        opponentMap.printOpponentMapAfterWin();
        System.out.print("\n");
        System.out.print("Your map:\n");
        gameMap.printMap();
    }

    protected void loop() {
        while(true) {
            try {
                if(!continueGame())
                    break;
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout");
                out.print(lastSentMessage);
                out.flush();
                retries++;
                if (retries >= 3) {
                    System.out.println("Communication error");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public abstract void start();
}
