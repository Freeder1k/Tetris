package main.output;

import main.Block;
import main.BlockQueue;
import main.Tetris;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SingleplayerInGame extends JPanel {
    private final JPanel[][] gameDisplay = new JPanel[Tetris.BOARD_HEIGHT - 2][Tetris.BOARD_WIDTH];
    private final JLabel scoreLabel;

    protected SingleplayerInGame() {

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        Font scoreFont = Output.getFont(null, -1, 20, scoreLabel.getFont());
        if (scoreFont != null)
            scoreLabel.setFont(scoreFont);
        this.add(scoreLabel, java.awt.BorderLayout.NORTH);

        JPanel gamePanel = new JPanel();
        this.add(gamePanel, java.awt.BorderLayout.CENTER);

        JPanel gamePanel2 = new JPanel();
        gamePanel2.setLayout(new java.awt.GridLayout(Tetris.BOARD_HEIGHT - 2, Tetris.BOARD_WIDTH));
        gamePanel2.setPreferredSize(new Dimension(Tetris.BOARD_WIDTH * 30, (Tetris.BOARD_HEIGHT - 2) * 30));
        gamePanel2.setMaximumSize(gamePanel2.getPreferredSize());
        gamePanel2.setMinimumSize(gamePanel2.getPreferredSize());

        Border border = BorderFactory.createLineBorder(Color.GRAY);
        for (int i1 = Tetris.BOARD_HEIGHT - 3; i1 >= 0; i1--) {
            for (int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2] = new JPanel();
                gameDisplay[i1][i2].setBorder(border);
                gameDisplay[i1][i2].setBackground(Color.BLACK);
                gamePanel2.add(gameDisplay[i1][i2]);
            }
        }
        gamePanel.add(gamePanel2);
    }

    protected synchronized void reset() {
        scoreLabel.setText("Score: 0");

        for (int i1 = Tetris.BOARD_HEIGHT - 3; i1 >= 0; i1--) {
            for (int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2].setBackground(Color.BLACK);
            }
        }
    }

    protected synchronized void update(Color[][] board, BlockQueue blockQueue, int score) {
        Block activeBlock = blockQueue.getActive();//TODO display queue
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
