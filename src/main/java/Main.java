package main.java;

import main.java.domain.GameField;

public class Main {
    public static void main(String[] args) {
        GameField gameField = new GameField(4);
        gameField.createField();
        System.out.println(gameField);
        gameField.moveBlocksUp();
        System.out.println(gameField);
        gameField.moveBlocksDown();
        System.out.println(gameField);


//        for (int i = 0; i < 5; i++) {
//            if (i % 2 == 0) {
//                gameField.moveBlocksLeft();
//                System.out.println("Left");
//            } else {
//                gameField.moveBlocksRight();
//                System.out.println("Right");
//            }
//            System.out.println(gameField);
//            gameField.generateRandomBlock();
//            System.out.println("New Block");
//            System.out.println(gameField);
//        }

    }
}