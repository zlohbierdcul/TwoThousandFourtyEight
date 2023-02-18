package main.java.facade;

import main.java.domain.GameField;

public class GameSystem {

    private GameField field;

    public GameSystem(int size) {
        this.field = new GameField(size);
    }

    public void startGame() {
        this.field.startGame();
    }

    public boolean spawnRandomBlock() {
        return this.field.generateRandomBlock();
    }

    public GameField getField() {
        return field;
    }

    public Integer getValue(int x, int y) {
        return field.getValue(x, y);
    }

    public void moveLeft() {
        this.field.moveBlocksLeft();
    }

    public void moveRight() {
        this.field.moveBlocksRight();
    }

    public void moveUp() {
        this.field.moveBlocksUp();
    }

    public void moveDown() {
        this.field.moveBlocksDown();
    }

    public boolean isFinished() {
        return this.field.isFinished();
    }
}
