package main.java.ui;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private CustomButton startButton;
    private JPanel mainPanel;

    public GUI() {
        setSize(800, 600);
        setLayout(new BorderLayout());
        mainMenu();
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


    public void mainMenu() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        GroupLayout groupLayout = new GroupLayout(mainPanel);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);
        mainPanel.setLayout(groupLayout);


        int size  = 4;
        startButton = new CustomButton("Start");
        CustomButton button = new CustomButton("ok");
        startButton.addActionListener(actionEvent -> {
            gameFrame(size);
        });
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addComponent(startButton).addComponent(startButton));
        mainPanel.add(startButton);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void gameFrame(int size) {
        System.out.println("Start");
    }


}
