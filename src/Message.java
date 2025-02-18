
import java.util.List;

public record Message(String command, int row, char col) {
    private static final List<String> VALID_STRINGS = List.of("start", "miss", "hit", "hit and sunk", "last ship sunk");

    public static Message parseMessage(String message) {
        String[] parts = message.split(";");
        return switch (parts.length){
            case 1 -> commandIsValid(parts[0]) ? new Message(parts[0], -1, (char) 0) : null;
            case 2 -> {
                if(!commandIsValid(parts[0])) {
                    yield null;
                }
                parts[1] = parts[1].trim();
                if(parts[1].length() > 3 || parts[1].length() < 2) {
                    yield null;
                }
                char col = parts[1].charAt(0);
                if(col < 'A' || col > 'J') {
                    yield null;
                }
                if(parts[1].length() == 3 && parts[1].charAt(1) == '1' && parts[1].charAt(2) == '0') {
                    yield new Message(parts[0], 10, col);
                }
                if(parts[1].length() == 2 && parts[1].charAt(1) >= '1' && parts[1].charAt(1) <= '9') {
                    yield new Message(parts[0], parts[1].charAt(1) - '1' + 1, col);
                }
                yield null;
            }
            default -> null;
        };
    }

    private static boolean commandIsValid(String part) {
        return VALID_STRINGS.contains(part);
    }

    public static int colAsInt(char col) {
        return col - 'A';
    }

    @Override
    public String toString() {
        return col == 0 && row == -1 ? (command + '\n') : (command + ';' + col + row + '\n');
    }
}