package domain;

import domain.util.Vector2D;
import org.graalvm.collections.Pair;

import java.util.*;

public class GameField implements Cloneable {

    private volatile HashMap<Vector2D, Integer> field;
    private final int size;
    private int score = 0;
    private boolean isStopped = false;
    private int spawns;

    public GameField(int size) {
        this.size = size;
    }

    public void setField(HashMap<Vector2D, Integer> field) {
        this.field = field;
    }

    public HashMap<Vector2D, Integer> getField() {
        return field;
    }

    public void startGame() {
        this.field = createGameField();
    }

    public void stopGame() {
        isStopped = true;
    }

    private HashMap<Vector2D, Integer> createGameField() {
        field = new HashMap<>();
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                field.put(new Vector2D(x,y), null);
            }
        }

        this.setValue(new Vector2D(0,0), 2);
        this.setValue(new Vector2D(1,0), 2);
        this.setValue(new Vector2D(2,0), 2);
        this.setValue(new Vector2D(3,0), 2);
        this.setValue(new Vector2D(0,1), 2);
        this.setValue(new Vector2D(1,1), 2);
        this.setValue(new Vector2D(2,1), 2);
        this.setValue(new Vector2D(3,1), 2);
//        this.setValue(new Vector2D(2,0), 2);
//        for (int i = 0; i < this.size / 2; i++) {
//            generateRandomBlock();
//        }
        return field;
    }

    public int getScore() {
        return score;
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
        System.out.println(spawns++);

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
        return true;
    }

    public Pair<Boolean, List<Vector2D>> moveBlocksLeft() {
        List<Boolean> movedRows = new ArrayList<>();
        List<Vector2D> vectors = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            Pair<Boolean, List<Vector2D>> pair = moveRowLeft(i);
            boolean moved = pair.getLeft();
            movedRows.add(moved);
            vectors.addAll(pair.getRight());
            if (moved) moveRowLeftNoMerge(i);

        }
        return Pair.create(movedRows.stream().anyMatch(Boolean::booleanValue), vectors);
    }

    private Pair<Boolean, List<Vector2D>> moveRowLeft(int y) {
        List<Vector2D> vectors = new ArrayList<>();
        boolean moved = false;
        for (int x = this.size - 1; x > 0; x--) {
            Vector2D firstVector = new Vector2D(x,y);
            Vector2D secondVector = new Vector2D(x-1,y);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListLeft(x + 1, this.size - 1, y);
                    x--;
                    score += Math.sqrt(getValue(secondVector));
                    moved = true;
                    vectors.add(secondVector);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListLeft(x, this.size - 1, y);
                moved = true;
            }
        }
        return Pair.create(moved, vectors);
    }

    private void moveRowLeftNoMerge(int y) {
        for (int x = this.size - 1; x > 0; x--) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x - 1, y);
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

    public Pair<Boolean, List<Vector2D>> moveBlocksRight() {
        List<Boolean> movedRows = new ArrayList<>();
        List<Vector2D> vectors = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            Pair<Boolean, List<Vector2D>> pair = moveRowRight(i);
            boolean moved = pair.getLeft();
            movedRows.add(moved);
            vectors.addAll(pair.getRight());
            if (moved) moveRowRightNoMerge(i);

        }
        return Pair.create(movedRows.stream().anyMatch(Boolean::booleanValue), vectors);
    }

    private Pair<Boolean, List<Vector2D>> moveRowRight(int y) {
        List<Vector2D> vectors = new ArrayList<>();
        boolean moved = false;
        for (int x = 0; x < this.size - 1; x++) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x + 1,y);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListRight(0, x - 1, y);
                    x++;
                    score += Math.sqrt(getValue(secondVector));
                    moved = true;
                    vectors.add(secondVector);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListRight(0, x, y);
                moved = true;
            }
        }
        return Pair.create(moved, vectors);
    }

    private void moveRowRightNoMerge(int y) {
        for (int x = 0; x < this.size - 1; x++) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x + 1, y);
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

    public Pair<Boolean, List<Vector2D>> moveBlocksUp() {
        List<Boolean> movedRows = new ArrayList<>();
        List<Vector2D> vectors = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            Pair<Boolean, List<Vector2D>> pair = moveRowUp(i);
            boolean moved = pair.getLeft();
            movedRows.add(moved);
            vectors.addAll(pair.getRight());
            if (moved) moveRowUpNoMerge(i);
        }
        return Pair.create(movedRows.stream().anyMatch(Boolean::booleanValue), vectors);
    }

    private Pair<Boolean, List<Vector2D>> moveRowUp(int x) {
        List<Vector2D> vectors = new ArrayList<>();
        boolean moved = false;
        for (int y = this.size - 1; y > 0; y--) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x,y - 1);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListUp(y + 1, this.size - 1, x);
                    y--;
                    score += Math.sqrt(getValue(secondVector));
                    moved = true;
                    vectors.add(secondVector);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListUp(y, this.size - 1, x);
                moved = true;
            }
        }
        return Pair.create(moved, vectors);
    }

    private void moveRowUpNoMerge(int x) {
        for (int y = this.size - 1; y > 0; y--) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x,y - 1);
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

    public Pair<Boolean, List<Vector2D>> moveBlocksDown() {
        List<Boolean> movedRows = new ArrayList<>();
        List<Vector2D> vectors = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            Pair<Boolean, List<Vector2D>> pair = moveRowDown(i);
            boolean moved = pair.getLeft();
            movedRows.add(moved);
            vectors.addAll(pair.getRight());
            if (moved) moveRowDownNoMerge(i, vectors);
        }
        System.out.println(movedRows);
        return Pair.create(movedRows.stream().anyMatch(Boolean::booleanValue), vectors);
    }

    private Pair<Boolean, List<Vector2D>> moveRowDown(int x) {
        List<Vector2D> vectors = new ArrayList<>();
        boolean moved = false;
        for (int y = 0; y < this.size - 1; y++) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x,y + 1);

            if (!isEmpty(firstVector) && !isEmpty(secondVector)) {
                if (getValue(firstVector).equals(getValue(secondVector))) {
                    setValue(secondVector, getValue(secondVector) * 2);
                    moveSubListDown(0, y - 1, x);
                    y++;
                    score += Math.sqrt(getValue(secondVector));
                    moved = true;
                    vectors.add(secondVector);
                }
            }

            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListDown(0, y, x);
                moved = true;
            }
        }

        return Pair.create(moved, vectors);
    }

    private void moveRowDownNoMerge(int x, List<Vector2D> vectors) {
        for (int y = 0; y < this.size - 1; y++) {
            Vector2D firstVector = new Vector2D(x, y);
            Vector2D secondVector = new Vector2D(x,y + 1);
            if (!isEmpty(firstVector) && isEmpty(secondVector)) {
                moveSubListDown(0, y, x);

                List<Vector2D> movedVector = vectors.stream().filter(v -> v.equals(firstVector)).toList();
                if (movedVector.size() > 0) {
                    System.out.println("movedVector: " + movedVector);
                    System.out.println("newVector: (" + x + ", " + y + ")" );
                    vectors.remove(movedVector.get(0));
                    vectors.add(secondVector);
                }
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

    public boolean hasPossibleMoves() {
        boolean isPossible = true;




        return isPossible;
    }

    public boolean isFinished() {
        return !this.hasPossibleMoves() || isStopped;
    }

    public boolean equals(GameField other) {
        boolean same = true;
        for (Vector2D vOther : other.field.keySet()) {
            if (!same) return false;
            Integer valueThis = this.field.get(vOther);
            Integer valueOther = other.field.get(vOther);
            if (valueThis == null && valueOther == null) continue;
            if (valueThis == null || valueOther == null) {
                same = false;
                continue;
            }
            if (!valueThis.equals(valueOther)) same = false;
        }
        return same;
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

    @Override
    public GameField clone() {
        try {
            GameField clone = (GameField) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
