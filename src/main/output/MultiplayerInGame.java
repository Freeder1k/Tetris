package main.output;

import main.Block;
import main.BlockQueue;
import main.Tetris;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MultiplayerInGame extends JPanel {
    private final JLabel scoreLabel;
    private final JPanel gamePanel2;
    private JPanel[][] gameDisplay = new JPanel[Tetris.BOARD_HEIGHT - 2][Tetris.BOARD_WIDTH];
    private int height, width;

    protected MultiplayerInGame(JPanel multiplayerBottomPanel) {
        this.setLayout(new BorderLayout());

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        Font scoreFont = Output.getFont(null, -1, 20, scoreLabel.getFont());
        if (scoreFont != null)
            scoreLabel.setFont(scoreFont);
        this.add(scoreLabel, BorderLayout.NORTH);

        JPanel gamePanel = new JPanel();
        this.add(gamePanel, BorderLayout.CENTER);

        gamePanel2 = new JPanel();
        gamePanel2.setLayout(new java.awt.GridLayout(Tetris.BOARD_HEIGHT - 2, Tetris.BOARD_WIDTH));
        gamePanel2.setPreferredSize(new Dimension(Tetris.BOARD_WIDTH * 30, (Tetris.BOARD_HEIGHT - 2) * 30));
        gamePanel2.setMaximumSize(gamePanel2.getPreferredSize());
        gamePanel2.setMinimumSize(gamePanel2.getPreferredSize());

        gamePanel.add(gamePanel2);

        this.add(multiplayerBottomPanel, BorderLayout.SOUTH);
    }

    protected synchronized void update(Color[][] board, BlockQueue blockQueue, int score) {
        Block activeBlock = blockQueue.getActive();//TODO display queue
        Color[][] blockOverlay = activeBlock.getOverlay();

        for (int i1 = 0; i1 < height - 2; i1++) {
            for (int i2 = 0; i2 < width; i2++) {
                if (blockOverlay[i1][i2] != Color.BLACK)
                    gameDisplay[i1][i2].setBackground(blockOverlay[i1][i2]);
                else
                    gameDisplay[i1][i2].setBackground(board[i1][i2]);
            }
        }

        scoreLabel.setText("Score: " + score);

        this.setVisible(true);
    }

    protected void resetBoard(int height, int width) {
        scoreLabel.setText("Score: 0");

        if (this.height != height || this.width != width) {
            gameDisplay = new JPanel[height - 2][width];
            this.height = height;
            this.width = width;

            Border border = BorderFactory.createLineBorder(Color.GRAY);
            for (int i1 = height - 3; i1 >= 0; i1--) {
                for (int i2 = 0; i2 < width; i2++) {
                    gameDisplay[i1][i2] = new JPanel();
                    gameDisplay[i1][i2].setBorder(border);
                    gameDisplay[i1][i2].setBackground(Color.BLACK);
                    gamePanel2.add(gameDisplay[i1][i2]);
                }
            }
        } else {
            for (int i1 = height - 3; i1 >= 0; i1--) {
                for (int i2 = 0; i2 < width; i2++) {
                    gameDisplay[i1][i2].setBackground(Color.BLACK);
                }
            }
        }
    }
}
