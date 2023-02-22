package facade;

import domain.GameField;
import domain.util.Vector2D;

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

    public void moveLeft() {
        this.field.moveBlocksLeft();
    }

    public void moveRight() {
        this.field.moveBlocksRight();
    }

    public void moveUp()  {
        this.field.moveBlocksUp();
    }

    public List<Vector2D> moveDown() {
        return  this.field.moveBlocksDown().getRight();
    }

    private boolean hasNoMoves() throws InterruptedException {
        AtomicBoolean checkUp = new AtomicBoolean(false);
        AtomicBoolean checkDown = new AtomicBoolean(false);
        AtomicBoolean checkLeft = new AtomicBoolean(false);
        AtomicBoolean checkRight = new AtomicBoolean(false);

        Thread checkUpThred = new Thread(() -> {
            System.out.println("T1");
            if (this.field.clone().moveBlocksUp()) checkUp.set(true);
        });

        Thread checkDownThred = new Thread(() -> {
            System.out.println("T2");
            if (this.field.clone().moveBlocksDown().getLeft()) checkUp.set(true);
        });

        Thread checkLeftThred = new Thread(() -> {
            System.out.println("T3");
            if (this.field.clone().moveBlocksLeft()) checkUp.set(true);
        });

        Thread checkRightThred = new Thread(() -> {
            System.out.println("T4");
            if (this.field.clone().moveBlocksRight()) checkUp.set(true);
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
