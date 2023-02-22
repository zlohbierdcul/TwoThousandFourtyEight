package gui;

import domain.util.Vector2D;
import facade.GameSystem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GUI extends JFrame {

    private final int SIZE = 4;
    private HashMap<Vector2D, CustomButton> gameBlocks = new HashMap<>();
    private JTextPane titlePane = new JTextPane();
    private JTextPane scorePane = new JTextPane();
    private CustomButton startButton = new CustomButton("Start");
    private CustomButton scoreButton = new CustomButton("Scores");
    private CustomButton exitButtonMenu = new CustomButton("Exit");
    private CustomButton exitButtonGame = new CustomButton("Exit");
    private CustomButton backButton = new CustomButton("Back");
    private Container menuContainer;
    private Container gameContainer;
    private Container scoreContainer;
    private GameSystem gameSystem;
    private Thread mainGameThread;
    private Thread thread2;
    private Container gameOverContainer;
    private CustomButton menuButton;
    private CustomButton retryButton;
    private JTextPane gameOverTextPane = new JTextPane();
    private AbstractButton resetButton = new CustomButton("Reset Scores");

    public GUI() {

        this.setSize(500, 800);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getGlassPane().setBackground(new Color(0,0,0));
        this.setFocusable(true);
        setDefaultLookAndFeelDecorated(true);
        this.mainMenu();
    }


    private void mainMenu() {

        menuContainer = new Container();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));

        // Startbutton
        startButton = new CustomButton("Start");
        startButton.setMaximumSize(new Dimension(300, 60));
        startButton.setFont(new Font("Fira Code", Font.BOLD,24));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setForeground(new Color(51,51,51));
        startButton.addActionListener(actionEvent -> {
            startGame(SIZE);
        });

        // Scorebutton
        scoreButton = new CustomButton("Scores");
        scoreButton.setMaximumSize(new Dimension(300, 60));
        scoreButton.setFont(new Font("Fira Code", Font.BOLD,24));
        scoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreButton.setForeground(new Color(51,51,51));
        scoreButton.addActionListener(actionEvent -> {
            try {
                scoreFrame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Exitbutton
        exitButtonMenu = new CustomButton("Exit");
        exitButtonMenu.setMaximumSize(new Dimension(300, 60));
        exitButtonMenu.setFont(new Font("Fira Code", Font.BOLD,24));
        exitButtonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButtonMenu.setForeground(new Color(51,51,51));
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
        menuContainer.add(startButton);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        menuContainer.add(scoreButton);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        menuContainer.add(exitButtonMenu);

        // draw this container
        this.getContentPane().removeAll();
        this.getContentPane().setVisible(false);
        this.getContentPane().add(menuContainer);
        this.getContentPane().setVisible(true);
        this.getContentPane().setBackground(new Color(51,51,51));
    }


    private void startGame(int size) {
        if (mainGameThread != null) mainGameThread.interrupt();
        // initialize GameSystem
        gameSystem = new GameSystem(size);
        gameSystem.startGame();
        this.gameFrame(size, new ArrayList<>());
        mainGameThread = new Thread(new Runnable() {
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
                        List<Vector2D> vectors = new ArrayList<>();
                        HashMap<Vector2D, Integer> oldfield = (HashMap<Vector2D, Integer>) gameSystem.getGameField().getField().clone();
                        if (!pressed) {
                            switch (keyCode) {
                                case KeyEvent.VK_UP:
                                    gameSystem.moveUp();
                                    break;
                                case KeyEvent.VK_DOWN:
                                    vectors.addAll(gameSystem.moveDown());
                                    break;
                                case KeyEvent.VK_LEFT:
                                    gameSystem.moveLeft();
                                    break;
                                case KeyEvent.VK_RIGHT:
                                    gameSystem.moveRight();
                                    break;
                                default: {
                                    return;
                                }
                            }
                            if (gameSystem.getGameField().hasPossibleMoves()) {
                                if (gameSystem.spawnRandomBlock()) {
                                    gameFrame(SIZE, vectors);
                                } else {
                                    stopGame();
                                    gameoverFrame();
                                    removeKeyListener(this);
                                }
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        pressed = false;
                    }
                });
            }
        });
        mainGameThread.start();
    }

    private void stopGame() {
        gameSystem.stopGame();
        this.removeKeyListener(getKeyListeners()[0]);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter("src/main/resources/scores", true)));
            writer.println(gameSystem.getScore());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void gameoverFrame() {

        gameOverContainer = new Container();
        gameOverContainer.setLayout(new BoxLayout(gameOverContainer, BoxLayout.Y_AXIS));
        gameOverContainer.removeAll();


        gameOverTextPane = new JTextPane();
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setFontSize(attributeSet, 32);
        StyleConstants.setFontFamily(attributeSet, "Fira Code");
        StyleConstants.setForeground(attributeSet, new Color(100,100,100));
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        StyledDocument doc = gameOverTextPane.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), attributeSet, false);
        gameOverTextPane.setCharacterAttributes(attributeSet, true);
        gameOverTextPane.setBackground(new Color(51,51,51));
        gameOverTextPane.setForeground(new Color(223, 194, 26));
        gameOverTextPane.setText("Game Over!!\n" + "Score: " + this.gameSystem.getScore());
        gameOverTextPane.setEditable(false);
        gameOverTextPane.setHighlighter(null);
        gameOverTextPane.setMaximumSize(new Dimension(this.getWidth(), 150));

        menuButton = new CustomButton("Menu");
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.setMaximumSize(new Dimension(200, 50));
        menuButton.setFont(new Font("Fire Code", 1, 22));
        menuButton.setForeground(new Color(80,80,80));
        menuButton.setEnabled(true);
        menuButton.addActionListener(ae -> { mainMenu(); });

        retryButton = new CustomButton("Retry");
        retryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryButton.setMaximumSize(new Dimension(200, 50));
        retryButton.setFont(new Font("Fire Code", 1, 22));
        retryButton.setForeground(new Color(80,80,80));
        retryButton.setEnabled(true);
        retryButton.addActionListener(ae -> { startGame(SIZE); });


        gameOverContainer.removeAll();
        gameOverContainer.add(Box.createRigidArea(new Dimension(getWidth(), getHeight()/3)));
        gameOverContainer.add(gameOverTextPane);
        gameOverContainer.add(Box.createRigidArea(new Dimension(getWidth(), 50)));
        gameOverContainer.add(menuButton);
        gameOverContainer.add(Box.createRigidArea(new Dimension(getWidth(), 20)));
        gameOverContainer.add(retryButton);


        this.getContentPane().removeAll();
        this.getContentPane().setVisible(false);
        this.getContentPane().add(gameOverContainer);
        this.getContentPane().setVisible(true);
    }

    private void scoreFrame() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/scores"));
        scoreContainer = new Container();
        scoreContainer.setLayout(new BoxLayout(scoreContainer, BoxLayout.Y_AXIS));

        JTextPane scoreTitle = new JTextPane();

        // scores text setup
        JTextPane scoresTextPane = new JTextPane();
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setFontSize(attributeSet, 24);
        StyleConstants.setFontFamily(attributeSet, "Fira Code");
        StyleConstants.setForeground(attributeSet, new Color(100,100,100));
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        StyledDocument doc = scoresTextPane.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), attributeSet, false);
        scoresTextPane.setCharacterAttributes(attributeSet, true);
        scoresTextPane.setMaximumSize(new Dimension((int) (getWidth() / 0.25), getHeight() / 2));
        scoresTextPane.setEditable(false);
        scoresTextPane.setHighlighter(null);
        scoresTextPane.setBackground(new Color(51,51,51));

        StyledDocument doc2 = scoreTitle.getStyledDocument();
        StyleConstants.setFontSize(attributeSet, 48);
        StyleConstants.setForeground(attributeSet, new Color(223, 194, 26));
        doc2.setParagraphAttributes(0, doc.getLength(), attributeSet, false);
        scoreTitle.setCharacterAttributes(attributeSet, true);
        scoreTitle.setMaximumSize(new Dimension(getWidth() / 2, getHeight() / 10));
        scoreTitle.setEditable(false);
        scoreTitle.setHighlighter(null);
        scoreTitle.setBackground(new Color(51,51,51));
        scoreTitle.setText("Scores:");

        List<Integer> collect = reader.lines().map(Integer::parseInt).sorted(Comparator.reverseOrder()).toList();

        for (int i = 1; i <= 10; i++) {
            String oldText = scoresTextPane.getText();
            if (collect.size() >= i) {
                scoresTextPane.setText(oldText + "\n" + i + ": " + collect.get(i-1));
            } else {
                scoresTextPane.setText(oldText + "\n" + "...");
                break;
            }
        }
        reader.close();

        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(200, 50));
        backButton.setFont(new Font("Fire Code", Font.BOLD, 22));
        backButton.setForeground(new Color(80,80,80));
        backButton.addActionListener(ae -> {
            mainMenu();
        });

        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setMaximumSize(new Dimension(150, 25));
        resetButton.setFont(new Font("Fire Code", Font.BOLD, 16));
        resetButton.setForeground(new Color(80,80,80));
        resetButton.addActionListener(ae -> {
            shakeButton(resetButton);
            File scores = new File("src/main/resources/scores");
            scores.delete();
            try {
                scores.createNewFile();
                scoreFrame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        scoreContainer.add(Box.createRigidArea(new Dimension(getWidth(), getHeight() / 10)));
        scoreContainer.add(scoreTitle);
        scoreContainer.add(scoresTextPane);
        scoreContainer.add(Box.createRigidArea(new Dimension(getWidth(), getHeight()/ 15)));
        scoreContainer.add(backButton);
        scoreContainer.add(Box.createRigidArea(new Dimension(getWidth(), getHeight()/ 25)));
        scoreContainer.add(resetButton);

        // drawing this container
        this.getContentPane().removeAll();
        this.getContentPane().setVisible(false);
        this.getContentPane().add(scoreContainer);
        this.getContentPane().setVisible(true);
    }

    private void gameFrame(int size, List<Vector2D> vectors) {
        // Container setup
        gameContainer = new Container();
        gameContainer.setLayout(new BoxLayout(gameContainer, BoxLayout.Y_AXIS));
        gameContainer.setBackground(new Color(56, 122, 187));

        // GameFieldPanel setup
        JPanel gamePanel = drawGameAndScorePanel(vectors);

        // ExitButton setup
        exitButtonGame = new CustomButton("Exit");
        exitButtonGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButtonGame.setMaximumSize(new Dimension(200, 50));
        exitButtonGame.setFont(new Font("Fire Code", 1, 22));
        exitButtonGame.setForeground(new Color(80,80,80));
        exitButtonGame.setEnabled(true);

        exitButtonGame.addActionListener(actionEvent -> {
            stopGame();
            mainMenu();
        });

        gameContainer.add(Box.createRigidArea(new Dimension(0, 50)));
        gameContainer.add(exitButtonGame);




        // drawing this container
        this.getContentPane().removeAll();
        this.getContentPane().setVisible(false);
        this.getContentPane().add(gameContainer);
        this.getContentPane().setVisible(true);
    }

    private JPanel drawGameAndScorePanel(List<Vector2D> vectors) {
        System.out.println(vectors);
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
        scorePane.setText("Score: " + this.gameSystem.getScore());
        scorePane.setEditable(false);
        scorePane.setHighlighter(null);
        scorePane.setMaximumSize(new Dimension(this.getWidth(), 50));
        gameContainer.add(Box.createRigidArea(new Dimension(0,50)));
        gameContainer.add(scorePane, Component.CENTER_ALIGNMENT);


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
//                newButton.setEnabled(false);
//                newButton.setContentAreaFilled(false);
                newButton.setFocusable(false);

                int finalX = x;
                int finalY = y;



                gamePanel.add(newButton);
                if (vectors.stream().anyMatch(v -> v.equals(new Vector2D(finalX, finalY)))) {
                    shakeButton(newButton);
                }
            }
        }

        gameContainer.add(Box.createRigidArea(new Dimension(0, 50)));
        gameContainer.add(gamePanel);
        return gamePanel;
    }


    private void shakeButton(AbstractButton button) {
        final Point point = button.getLocation();
        final int delay = 40;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    try {

                        button.setBorder(BorderFactory.createLineBorder(button.getBackground(), 1, true));
                        button.setMaximumSize(new Dimension(105, 105));
                        Thread.sleep(delay);
                        button.setBorder(BorderFactory.createLineBorder(button.getBackground(), 3, true));
                        button.setMaximumSize(new Dimension(106, 106));
                        Thread.sleep(delay);
                        button.setBorder(BorderFactory.createLineBorder(button.getBackground(), 2, true));
                        button.setMaximumSize(new Dimension(103, 103));
                        Thread.sleep(delay);
                        button.setBorder(BorderFactory.createLineBorder(button.getBackground(), 1, true));
                        button.setMaximumSize(new Dimension(100, 100));
                        Thread.sleep(delay);
                        button.setBorder(null);

//                        moveButton(button, new Point(point.x + 2, point.y));
//                        moveButton(button, point);
//                        Thread.sleep(delay);
//                        moveButton(button, new Point(point.x - 2, point.y));
//                        Thread.sleep(delay);
//                        moveButton(button, point);
//                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void moveButton(AbstractButton button, final Point p) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                button.setLocation(p);
            }
        });
    }
}
