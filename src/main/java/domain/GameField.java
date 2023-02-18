package domain;

import domain.util.Vector2D;

import java.util.*;

public class GameField {

    private volatile HashMap<Vector2D, Integer> field;
    private final int size;

    public GameField(int size) {
        this.size = size;
    }

    public void startGame() {
        this.field = createGameField();
    }

    private HashMap<Vector2D, Integer> createGameField() {
        field = new HashMap<>();
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                field.put(new Vector2D(x,y), null);
            }
        }

        for (int i = 0; i < this.size; i++) {
            generateRandomBlock();
        }
        return field;
    }

    public synchronized Integer getValue(Vector2D v) {
        return getFieldVector(v).map(d -> this.field.get(d)).orElse(null);
    }

    public Integer getValue(int x, int y) {
        return getValue(new Vector2D(x, y));
    }

    public void setValue(Vector2D v, Integer i) {
        Optional<Vector2D> vector2D = getFieldVector(v);

        vector2D.ifPresentOrElse(d -> this.field.put(d, i), () -> this.field.put(new Vector2D(v.getX(), v.getY()), i));
    }

    public void removeValue(Vector2D v) {
        Optional<Vector2D> vector2D = getFieldVector(v);

        vector2D.ifPresent(d -> this.field.remove(d));
    }

    private Optional<Vector2D> getFieldVector(Vector2D v) {
        Set<Vector2D> vector2DS = this.field.keySet();
        int finalY = v.getY();
        int finalX = v.getX();
        return vector2DS.stream().filter(vec -> vec.getX() == finalX && vec.getY() == finalY).findAny();
    }

    public boolean isEmpty(Vector2D vector2D) {
        return getValue(vector2D) == null;
    }

    public boolean generateRandomBlock() {

        Random random = new Random();
        boolean notFinished = true;
        Vector2D testVector = new Vector2D();
        int trys = 0;

        while (notFinished) {
            trys++;
            if (trys > 10000) return false;
            int x = random.nextInt(0, size);
            int y = random.nextInt(0, size);

            testVector = new Vector2D(x,y);

            if (isEmpty(testVector)) {
                setValue(testVector, Math.random() > 0.5 ? 2: 4);
                notFinished = false;
            }
        }
        System.out.println(testVector);
        return true;
    }

    public void moveBlocksLeft() {
        for (int i = 0; i < this.size; i++) {
            moveRowLeft(i);
        }
    }

    private void moveRowLeft(int y) {
        for (int x = this.size - 1; x > 0; x--) {
            Vector2D firstVector = new Vector2D(x,y);
            Vector2D secondVector = new Vector2D(x-1,y);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListLeft(x + 1, this.size - 1, y);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListLeft(x, this.size - 1, y);
            }
        }
    }

    private void moveSubListLeft(int start, int end, int column) {
        for (int i = start; i <= end; i++) {
            if (getValue(new Vector2D(i, column)) == null) {
                setValue(new Vector2D(i - 1, column), null);
            } else {
                setValue(new Vector2D(i - 1, column), getValue(new Vector2D(i, column)));
            }
        }
        setValue(new Vector2D(end, column), null);
    }

    public void moveBlocksRight() {
        for (int i = 0; i < this.size; i++) {
            moveRowRight(i);
        }
    }

    private void moveRowRight(int y) {
        for (int x = 0; x < this.size - 1; x++) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x + 1,y);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListRight(0, x - 1, y);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListRight(0, x, y);
            }
        }
    }

    private void moveSubListRight(int start, int end, int column) {
        for (int i = end; i >= start; i--) {
            if (getValue(new Vector2D(i, column)) == null) {
//                System.out.println(i + ", " + column);
                setValue(new Vector2D(i + 1, column), null);
            } else {
                setValue(new Vector2D(i + 1, column), getValue(new Vector2D(i, column)));

            }
        }
        setValue(new Vector2D(start, column), null);
    }

    public void moveBlocksUp() {
        for (int i = 0; i < this.size; i++) {
            moveRowUp(i);
        }
    }

    private void moveRowUp(int x) {
        for (int y = this.size - 1; y > 0; y--) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x,y - 1);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListUp(y + 1, this.size - 1, x);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListUp(y, this.size - 1, x);
            }
        }
    }

    private void moveSubListUp(int start, int end, int row) {
        for (int i = start; i <= end; i++) {
            if (getValue(new Vector2D(row, i)) == null) {
                setValue(new Vector2D(row, i - 1), null);
            } else {
                setValue(new Vector2D(row, i - 1), getValue(new Vector2D(row, i)));
            }
        }
        setValue(new Vector2D(row, end), null);
    }

    public void moveBlocksDown() {
        for (int i = 0; i < this.size; i++) {
            moveRowDown(i);
        }
    }

    private void moveRowDown(int x) {
        for (int y = 0; y < this.size - 1; y++) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x,y + 1);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListDown(0, y - 1, x);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListDown(0, y, x);
            }
        }
    }

    private void moveSubListDown(int start, int end, int row) {
        for (int i = end; i >= start; i--) {
            if (getValue(new Vector2D(row, i)) == null) {
                setValue(new Vector2D(row, i + 1), null);
            } else {
                setValue(new Vector2D(row, i + 1), getValue(new Vector2D(row, i)));
            }
        }
        setValue(new Vector2D(row, start), null);
    }

    public boolean isFinished() {
        return this.field.values().stream().noneMatch(Objects::isNull);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("----".repeat(this.size));
        sb.append("-\n");

        for (int y = 0; y < this.size; y++) {
            sb.append("|");
            for (int x = 0; x < this.size; x++) {
                Integer value = getValue(new Vector2D(x, y));
                if (value != null) {
                    sb.append(" ").append(value).append(" ");
                } else {
                    sb.append(" ").append(" ").append(" ");
                }

                sb.append("|");
            }
            sb.append("\n");
            sb.append("----".repeat(this.size));
            sb.append("-");
            sb.append("\n");
        }
        return sb.toString();
    }

}
