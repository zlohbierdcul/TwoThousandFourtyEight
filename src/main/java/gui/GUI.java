package main.java.gui;

import main.java.facade.GameSystem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame {

    private final int SIZE = 4;
    private JTextPane titlePane = new JTextPane();
    private JTextPane scorePane = new JTextPane();
    private CustomButton startButton = new CustomButton("Start");
    private CustomButton exitButtonMenu = new CustomButton("Exit");
    private CustomButton exitButtonGame = new CustomButton("Exit");
    private Container menuContainer;
    private Container gameContainer;
    private GameSystem gameSystem;
    private Thread thread;
    private Thread thread2;

    public GUI() {

        setSize(500, 800);
        setResizable(false);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainMenu();
    }


    public void mainMenu() {

        menuContainer = new Container();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));

        // Startbutton
        startButton.setMaximumSize(new Dimension(300, 60));
        startButton.setFont(new Font("Fira Code", Font.BOLD,24));
        startButton.addActionListener(actionEvent -> {
            startGame(SIZE);
        });

        // Exitbutton
        exitButtonMenu.setMaximumSize(new Dimension(300, 60));
        exitButtonMenu.setFont(new Font("Fira Code", Font.BOLD,24));
        exitButtonMenu.addActionListener(actionEvent -> {
            setVisible(false);
        });

        // Attributes for the TextPane / Title
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setFontSize(attributeSet, 68);
        StyleConstants.setFontFamily(attributeSet, "Fira Code");
        StyleConstants.setForeground(attributeSet, new Color(100,100,100));
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        StyledDocument doc = titlePane.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), attributeSet, false);
        titlePane.setCharacterAttributes(attributeSet, true);
        titlePane.setMaximumSize(new Dimension(getWidth() / 2, getHeight() / 5));
        titlePane.setText("2048");
        titlePane.setEditable(false);
        titlePane.setHighlighter(null);
        titlePane.setBackground(new Color(51,51,51));


        // adding everything to container
        menuContainer.add(Box.createRigidArea(new Dimension(getWidth(), (int) (getHeight() / 4))));
        menuContainer.add(titlePane, Component.CENTER_ALIGNMENT);
        addAButton(startButton, menuContainer);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        addAButton(exitButtonMenu, menuContainer);

        // draw this container
        this.getContentPane().removeAll();
        this.getContentPane().setVisible(false);
        this.getContentPane().add(menuContainer);
        this.getContentPane().setVisible(true);
        this.getContentPane().setBackground(new Color(51,51,51));
    }


    private void startGame(int size) {
        // initialize GameSystem
        gameSystem = new GameSystem(size);
        gameSystem.startGame();
        this.gameFrame(size);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                addKeyListener(new KeyListener() {
                    boolean pressed = false;
                    @Override
                    public void keyTyped(KeyEvent e) {
                        System.out.println("Typed");
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        int keyCode = e.getKeyCode();
                        if (!pressed) {
                            switch (keyCode) {
                                case KeyEvent.VK_UP -> gameSystem.moveUp();
                                case KeyEvent.VK_DOWN -> gameSystem.moveDown();
                                case KeyEvent.VK_LEFT -> gameSystem.moveLeft();
                                case KeyEvent.VK_RIGHT -> gameSystem.moveRight();
                                default -> {
                                    return;
                                }
                            }
                            if (gameSystem.spawnRandomBlock()) {
                                gameFrame(SIZE);
                            } else {
                                mainMenu();
                                removeKeyListener(this);
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        pressed = false;
                        System.out.println("Released.");
                    }
                });
            }
        });
        thread.start();


    }

    private void gameFrame(int size) {
        // Container setup
        gameContainer = new Container();
        gameContainer.setLayout(new BoxLayout(gameContainer, BoxLayout.Y_AXIS));
        gameContainer.setBackground(new Color(56, 122, 187));

        // ScorePane setup
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setFontSize(attributeSet, 32);
        StyleConstants.setFontFamily(attributeSet, "Fira Code");
        StyleConstants.setForeground(attributeSet, new Color(100,100,100));
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        StyledDocument doc = scorePane.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), attributeSet, false);
        scorePane.setCharacterAttributes(attributeSet, true);
        scorePane.setBackground(new Color(51,51,51));
        scorePane.setForeground(new Color(80,80,80));
        scorePane.setText("Score: 0");
        scorePane.setEditable(false);
        scorePane.setHighlighter(null);
        scorePane.setMaximumSize(new Dimension(this.getWidth(), 50));
        gameContainer.add(Box.createRigidArea(new Dimension(0,50)));
        gameContainer.add(scorePane, Component.CENTER_ALIGNMENT);


        // GameFieldPanel setup
        JPanel gamePanel = drawGameField();

        // ExitButton setup
        exitButtonGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButtonGame.setMaximumSize(new Dimension(200, 50));
        exitButtonGame.setFont(new Font("Fire Code", 1, 22));
        exitButtonGame.setForeground(new Color(80,80,80));

        exitButtonGame.addActionListener(actionEvent -> {
            mainMenu();
        });

        gameContainer.add(Box.createRigidArea(new Dimension(0, 50)));
        gameContainer.add(exitButtonGame);


        this.setFocusable(true);

        final boolean[] timeout = {false};

        // drawing this container
        this.getContentPane().removeAll();
        this.getContentPane().setVisible(false);
        this.getContentPane().add(gameContainer);
        this.getContentPane().setVisible(true);



    }

    private void gameLoop() {
        mainloop:
        while(true) {
            if (gameSystem.isFinished()) {
                mainMenu();
                break mainloop;
            }
        }

    }


    private JPanel drawGameField() {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(SIZE, SIZE, 20, 20));
        gamePanel.setBackground(new Color(80,80,80));
        Border border = BorderFactory.createLineBorder(new Color(80,80,80), 15, true);
        gamePanel.setBorder(border);
        gamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.setMaximumSize(new Dimension((int) (getWidth() / 1.2), (int) (getWidth() / 1.2)));
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Integer num = gameSystem.getValue(x,y);
                CustomButton newButton;

                if (num != null) {
                    float sat = (float) Math.log(num) / 10;
                    newButton = new CustomButton("0", Color.getHSBColor(0.03F, sat, 1));
                    newButton.setText(Integer.toString(num));
                } else {
                    newButton = new CustomButton("0", new Color(255,255,255));
                }


                newButton.setMaximumSize(new Dimension(100, 100));
                newButton.setFont(new Font("Fire Code", 1, 26));
                newButton.setForeground(new Color(50,50,50));
                newButton.setEnabled(false);



                gamePanel.add(newButton);
            }
        }

//        gameContainer.remove(gamePanel);
        gameContainer.add(Box.createRigidArea(new Dimension(0, 50)));
        gameContainer.add(gamePanel);
        return gamePanel;
    }


    private static void addAButton(CustomButton button, Container container) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(button);
    }
}
