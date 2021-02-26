package main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Output extends JFrame {
    private final JLabel scoreLabel;
    private final Color[][] board;
    private final JPanel[][] gameDisplay = new JPanel[20][10];

    public Output(Color[][] board, InputListener inputListener) {
        this.board = board;
        this.setSize(350, 700);

        this.setTitle("main");
        JPanel panel = new JPanel();

        panel.setLayout(new java.awt.BorderLayout());

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        JPanel gamePanel = new JPanel();

        gamePanel.setLayout(new java.awt.GridLayout(Tetris.BOARD_HEIGHT - 2, Tetris.BOARD_WIDTH));
        gamePanel.setPreferredSize(new Dimension(Tetris.BOARD_WIDTH * 30, (Tetris.BOARD_HEIGHT - 2) * 30));
        gamePanel.setMaximumSize(gamePanel.getPreferredSize());
        gamePanel.setMinimumSize(gamePanel.getPreferredSize());

        Border border = BorderFactory.createLineBorder(Color.gray);
        for (int i1 = Tetris.BOARD_HEIGHT - 3; i1 >= 0; i1--) {
            for (int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2] = new JPanel();
                gameDisplay[i1][i2].setBorder(border);
                gameDisplay[i1][i2].setBackground(Color.BLACK);
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
        Color[][] blockOverlay = activeBlock.getOverlay();

        for (int i1 = 0; i1 < Tetris.BOARD_HEIGHT - 2; i1++) {
            for (int i2 = 0; i2 < Tetris.BOARD_WIDTH; i2++) {
                if (blockOverlay[i1][i2] != Color.BLACK)
                    gameDisplay[i1][i2].setBackground(blockOverlay[i1][i2]);
                else
                    gameDisplay[i1][i2].setBackground(board[i1][i2]);
            }
        }

        scoreLabel.setText("Score: " + score);

        this.setVisible(true);
    }
}