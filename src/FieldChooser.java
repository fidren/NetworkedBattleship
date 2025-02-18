import java.util.Random;

public class FieldChooser {
    private int lastSentRow;
    private int lastSentCol;
    private int currentRow;
    private int currentCol;
    ChooserType chooserType;
    Random random;

    public FieldChooser(ChooserType type) {
        currentRow = 0;
        currentCol = 0;
        chooserType = type;
        random = new Random();
    }

    public int getLastSentRow() {
        return lastSentRow;
    }

    public int getLastSentCol() {
        return lastSentCol;
    }

    public String chooseField() {
        return chooserType == ChooserType.RANDOM ? chooseRandomField() : chooseOneByOneField();
    }

    private String chooseOneByOneField() {
        lastSentRow = currentRow;
        lastSentCol = currentCol;

        if(currentRow == 9 && currentCol == 9) {
            currentRow = 0;
            currentCol = 0;
        }

        if(currentCol == 9){
            currentCol = 0;
            currentRow++;
        } else {
            currentCol++;
        }

        char col = (char) (lastSentCol + 'A');

        return col + String.valueOf(lastSentRow+1);
    }

    private String chooseRandomField() {
        int row = random.nextInt(10) + 1;
        char col = (char) (random.nextInt(10) + 'A');

        lastSentRow = row - 1;
        lastSentCol = Message.colAsInt(col);

        return col + String.valueOf(row);
    }
}
