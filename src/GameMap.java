import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GameMap {
    private final char[][] board;
    private int hashAmount;

    public GameMap(char[][] board, int hashAmount) {
        this.board = board;
        this.hashAmount = hashAmount;
    }

    public static GameMap getMapInstanceFromFile(Path path) {
        char[][] board = new char[10][10];
        try {
            List<String> strings = Files.readAllLines(path);
            for (int i = 0; i < strings.size(); i++) {
                board[i] = strings.get(i).toCharArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new GameMap(board, 20);
    }

    public static GameMap getUnknownMapInstance() {
        char[][] board = new char[10][10];
        for(char[] row : board) {
            Arrays.fill(row, '?');
        }
        return new GameMap(board, 20);
    }

    public void printMap() {
        for (char[] chars : board) {
            for (char singleChar : chars) {
                System.out.print(singleChar);
            }
            System.out.println();
        }
    }

    public void printOpponentMapAfterWin() {
        for (char[] chars : board) {
            for (char singleChar : chars) {
                System.out.print(singleChar == '?' ? '.' : singleChar);
            }
            System.out.println();
        }
    }

    public String getResult(int row, int col) {
        if(board[row][col] == '.' || board[row][col] == '~') {
            board[row][col] = '~';
            return "miss";
        }

        Set<Coordinate> shipCoordinates = findShip(row, col);

        switch(shipCoordinates.size()) {
            case 1 -> {
                if(board[row][col] == '#')
                    hashAmount--;
                board[row][col] = '@';
                return hashAmount == 0 ? "last ship sunk" : "hit and sunk";
            }
            case 2,3,4 -> {
                long atSignCount = shipCoordinates.stream()
                        .filter(coordinate -> board[coordinate.row()][coordinate.col()] == '@')
                        .count();

                if (atSignCount == shipCoordinates.size())
                    return "hit and sunk";


                if(board[row][col] == '#')
                    hashAmount--;
                board[row][col] = '@';

                if (atSignCount == shipCoordinates.size() - 1){
                    return hashAmount == 0 ? "last ship sunk" : "hit and sunk";
                }

                return "hit";
            }
            default -> throw new IllegalStateException("Unexpected value: " + shipCoordinates.size());
        }
    }

    private Set<Coordinate> findShip(int startRow, int startCol) {
        Set<Coordinate> shipCoordinates = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(new Coordinate(startRow, startCol));

        while (!queue.isEmpty()) {
            Coordinate coordinate = queue.poll();
            int row = coordinate.row();
            int col = coordinate.col();

            shipCoordinates.add(coordinate);
            if(row - 1 >= 0 && (board[row - 1][col] == '#' || board[row - 1][col] == '@')){
                Coordinate temp = new Coordinate(row - 1, col);
                if (!shipCoordinates.contains(temp))
                    queue.add(temp);
            }
            if(col - 1 >= 0 && (board[row][col - 1] == '#' || board[row][col - 1] == '@')) {
                Coordinate temp = new Coordinate(row, col - 1);
                if (!shipCoordinates.contains(temp))
                    queue.add(temp);
            }
            if(row + 1 < 10 && (board[row + 1][col] == '#' || board[row + 1][col] == '@')) {
                Coordinate temp = new Coordinate(row + 1, col);
                if (!shipCoordinates.contains(temp))
                    queue.add(temp);
            }
            if(col + 1 < 10 && (board[row][col + 1] == '#' || board[row][col + 1] == '@')) {
                Coordinate temp = new Coordinate(row, col + 1);
                if (!shipCoordinates.contains(temp))
                    queue.add(temp);
            }
        }
        return shipCoordinates;
    }

    public void processCommand(String command, int row, int col) {
        switch (command) {
            case "miss" -> board[row][col] = '.';
            case "hit" -> board[row][col] = '#';
            case "hit and sunk", "last ship sunk" -> {
                int[][] coordinatesSurroundedShip = {
                        {-1, -1}, {-1, 0}, {-1, 1},
                        {0, -1}, {0, 1},
                        {1, -1}, {1, 0}, {1, 1}
                };

                board[row][col] = '#';
                Set<Coordinate> shipCoordinates = findShip(row, col);

                for (Coordinate coordinate : shipCoordinates) {
                    for (int[] pair : coordinatesSurroundedShip) {
                        int currentRow = coordinate.row() + pair[0];
                        int currentCol = coordinate.col() + pair[1];
                        if (currentRow < 10 &&
                                currentRow >= 0 &&
                                currentCol < 10 &&
                                currentCol >= 0 &&
                                board[currentRow][currentCol] != '#'
                        ) {
                            board[currentRow][currentCol] = '.';
                        }
                    }
                }
            }
        }
    }
}
