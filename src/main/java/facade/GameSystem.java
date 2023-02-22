package facade;

import domain.GameField;
import domain.util.Vector2D;
import org.graalvm.collections.Pair;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameSystem {

    private GameField field;

    public GameSystem(int size) {
        this.field = new GameField(size);
    }

    public void startGame() {
        this.field.startGame();
    }

    public void stopGame() {
        this.field.stopGame();
    }

    public boolean spawnRandomBlock() {
        return this.field.generateRandomBlock();
    }

    public GameField getGameField() {
        return this.field;
    }

    public void setGameField(GameField field) {
        this.field = field;
    }

    public Integer getValue(int x, int y) {
        return field.getValue(x, y);
    }

    public int getScore() {
        return this.field.getScore();
    }

    public Pair<Boolean,List<Vector2D>> moveLeft() {
        Pair<Boolean, List<Vector2D>> booleanListPair = this.field.moveBlocksLeft();
        return Pair.create(booleanListPair.getLeft(), booleanListPair.getRight());
    }

    public Pair<Boolean,List<Vector2D>> moveRight() {
        return this.field.moveBlocksRight();
    }

    public Pair<Boolean,List<Vector2D>> moveUp()  {
        Pair<Boolean, List<Vector2D>> booleanListPair = this.field.moveBlocksUp();
        return Pair.create(booleanListPair.getLeft(), booleanListPair.getRight());
    }

    public Pair<Boolean,List<Vector2D>> moveDown() {
        Pair<Boolean, List<Vector2D>> booleanListPair = this.field.moveBlocksDown();
        return Pair.create(booleanListPair.getLeft(), booleanListPair.getRight());
    }

    private boolean hasNoMoves() throws InterruptedException {
        AtomicBoolean checkUp = new AtomicBoolean(false);
        AtomicBoolean checkDown = new AtomicBoolean(false);
        AtomicBoolean checkLeft = new AtomicBoolean(false);
        AtomicBoolean checkRight = new AtomicBoolean(false);

        Thread checkUpThred = new Thread(() -> {
            System.out.println("T1");
            if (this.field.clone().moveBlocksUp().getLeft()) checkUp.set(true);
        });

        Thread checkDownThred = new Thread(() -> {
            System.out.println("T2");
            if (this.field.clone().moveBlocksDown().getLeft()) checkUp.set(true);
        });

        Thread checkLeftThred = new Thread(() -> {
            System.out.println("T3");
            if (this.field.clone().moveBlocksLeft().getLeft()) checkUp.set(true);
        });

        Thread checkRightThred = new Thread(() -> {
            System.out.println("T4");
            if (this.field.clone().moveBlocksRight().getLeft()) checkUp.set(true);
        });

        checkUpThred.start();
        checkDownThred.start();
        checkLeftThred.start();
        checkRightThred.start();

        checkUpThred.join();
        checkDownThred.join();
        checkLeftThred.join();
        checkRightThred.join();

        return checkUp.get() && checkDown.get() && checkLeft.get() && checkRight.get();
    }

    public boolean isFinished() throws InterruptedException {
        return this.hasNoMoves();
    }
}
