package main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Output extends JFrame {
    private final JPanel panel;
    private final JPanel startPanel;
    private final JPanel optionPanel;
    private final JPanel gamePanel;
    private final JPanel gameOverPanel;
    private final JLabel gameOverScoreLabel;
    private final JLabel scoreLabel;
    private final Color[][] board;
    private final JPanel[][] gameDisplay = new JPanel[20][10];
    private final InputListener inputListener;

    public Output(Color[][] board, InputListener inputListener, Tetris tetris) {
        this.board = board;
        this.inputListener = inputListener;

        this.setSize(Tetris.BOARD_WIDTH * 30 + 50, Tetris.BOARD_HEIGHT * 30 + 50);
        this.setResizable(false);

        this.setTitle("Tetris");
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        this.add(panel);

        //start menu
        JLabel tetrisLabel = new JLabel("TETRIS");
        Font tetrisLabelFont = this.getFont("Rockwell Extra Bold", Font.BOLD, 72, tetrisLabel.getFont());
        if (tetrisLabelFont != null)
            tetrisLabel.setFont(tetrisLabelFont);
        tetrisLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tetrisLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        panel.add(tetrisLabel, BorderLayout.CENTER);

        startPanel = new JPanel();
        startPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 50));
        panel.add(startPanel, BorderLayout.SOUTH);

        JButton startButton = new JButton();
        Font startButtonFont = getFont(null, -1, 48, startButton.getFont());
        if (startButtonFont != null)
            startButton.setFont(startButtonFont);
        startButton.setHorizontalTextPosition(SwingConstants.CENTER);
        startButton.setText("Start");
        startButton.addActionListener(e -> {
            setToInGameDisplay();
            tetris.start();
        });
        startPanel.add(startButton);

        optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panel.add(optionPanel, BorderLayout.NORTH);

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BorderLayout());
        optionPanel.add(colorPanel);

        JLabel colorLabel = new JLabel();
        colorLabel.setText("Color Mode:");
        colorPanel.add(colorLabel, BorderLayout.NORTH);

        JComboBox<String> colorBox = new JComboBox<>();
        colorBox.addItem("Standard");
        colorBox.addItem("Blind Mode");
        colorBox.addItem("Rainbow Mode");
        colorBox.setToolTipText("Color");
        colorPanel.add(colorBox, BorderLayout.WEST);


        //in-game display
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        Font scoreFont = getFont(null, -1, 22, scoreLabel.getFont());
        if (scoreFont != null) scoreLabel.setFont(scoreFont);

        JPanel gamePanel2 = new JPanel();
        gamePanel2.setLayout(new java.awt.GridLayout(Tetris.BOARD_HEIGHT - 2, Tetris.BOARD_WIDTH));
        gamePanel2.setPreferredSize(new Dimension(Tetris.BOARD_WIDTH * 30, (Tetris.BOARD_HEIGHT - 2) * 30));
        gamePanel2.setMaximumSize(gamePanel2.getPreferredSize());
        gamePanel2.setMinimumSize(gamePanel2.getPreferredSize());

        Border border = BorderFactory.createLineBorder(Color.gray);
        for (int i1 = Tetris.BOARD_HEIGHT - 3; i1 >= 0; i1--) {
            for (int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2] = new JPanel();
                gameDisplay[i1][i2].setBorder(border);
                gameDisplay[i1][i2].setBackground(Color.BLACK);
                gamePanel2.add(gameDisplay[i1][i2]);
            }
        }

        gamePanel = new JPanel();
        gamePanel.add(gamePanel2);


        //game over menu
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BorderLayout());

        gameOverScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        if (scoreFont != null) gameOverScoreLabel.setFont(scoreFont);
        gameOverPanel.add(gameOverScoreLabel, BorderLayout.SOUTH);

        JLabel gameOverLabel = new JLabel("<html>GAME<br/>OVER</html>");
        if (tetrisLabelFont != null)
            gameOverLabel.setFont(tetrisLabelFont);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        gameOverPanel.add(gameOverLabel, BorderLayout.CENTER);


        this.setVisible(true);

        this.addWindowListener(inputListener);
    }

    public synchronized void updateOutput(Block activeBlock, int score) {
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

    private synchronized void setToInGameDisplay() {
        panel.removeAll();
        panel.repaint();

        for (int i1 = Tetris.BOARD_HEIGHT - 3; i1 >= 0; i1--) {
            for (int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2].setBackground(Color.BLACK);
            }
        }

        panel.add(scoreLabel, java.awt.BorderLayout.PAGE_START);
        panel.add(gamePanel, java.awt.BorderLayout.CENTER);
        this.requestFocusInWindow();
        this.addKeyListener(inputListener);

        this.setVisible(true);
    }

    public synchronized void setToGameOverMenu() {
        panel.removeAll();
        panel.repaint();
        this.removeKeyListener(inputListener);

        gameOverScoreLabel.setText(scoreLabel.getText());

        panel.add(startPanel, BorderLayout.SOUTH);

        panel.add(optionPanel, BorderLayout.NORTH);

        panel.add(gameOverPanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    private Font getFont(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }
}