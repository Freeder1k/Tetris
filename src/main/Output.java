package main;

import javax.swing.*;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Dimension;
import java.util.EventListener;

public class Output extends JFrame {
    private final JLabel scoreLabel;
    private final boolean[][] board;
    private final JPanel[][] gameDisplay = new JPanel[20][10];
    private JLabel label;
    private JTextField tf;

    public Output(boolean[][] board, InputListener inputListener) {
        this.board = board;
        this.setSize(350, 700);

        this.setTitle("main");
        JPanel panel = new JPanel();

        panel.setLayout(new java.awt.BorderLayout());

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        JPanel gamePanel = new JPanel();

        gamePanel.setLayout(new java.awt.GridLayout(20, 10));
        gamePanel.setPreferredSize(new Dimension(300, 600));
        gamePanel.setMaximumSize(gamePanel.getPreferredSize());
        gamePanel.setMinimumSize(gamePanel.getPreferredSize());

        Border border = BorderFactory.createLineBorder(Color.gray);
        for (int i1 = 0; i1 < 20; i1++) {
            for (int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2] = new JPanel();
                gameDisplay[i1][i2].setBorder(border);
                gameDisplay[i1][i2].setBackground(Color.black);
                gamePanel.add(gameDisplay[i1][i2]);
            }
        }

        JPanel midPanel = new JPanel();
        midPanel.add(gamePanel);

        panel.add(scoreLabel, java.awt.BorderLayout.PAGE_START);
        panel.add(midPanel, java.awt.BorderLayout.CENTER);

        this.add(panel);
        this.setVisible(true);

        this.addKeyListener(inputListener);
        this.addWindowListener(inputListener);
    }

    public void updateOutput(Block activeBlock, int score) {
        boolean[][] blockOverlay = activeBlock.getOverlay();

        for (int i1 = 0; i1 < 20; i1++) {
            for (int i2 = 0; i2 < 10; i2++) {
                Color color = blockOverlay[i1][i2] ? Color.LIGHT_GRAY : (board[i1][i2] ? Color.WHITE : Color.BLACK);
                gameDisplay[i1][i2].setBackground(color);
            }
        }

        scoreLabel.setText("Score: " + score);

        this.setVisible(true);
    }
}