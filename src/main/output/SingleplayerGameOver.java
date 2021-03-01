package main.output;

import main.Tetris;

import javax.swing.*;
import java.awt.*;

public class SingleplayerGameOver extends JPanel {
    JLabel gameOverScoreLabel;

    protected SingleplayerGameOver(Output output, Tetris tetris, Font titleFont, Font buttonFont, JPanel colorOptionPanel) {
        this.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel("<html><center>GAME<br/>OVER</center></html>");
        if (titleFont != null)
            gameOverLabel.setFont(titleFont);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        this.add(gameOverLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        this.add(buttonPanel, BorderLayout.SOUTH);

        gameOverScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        Font scoreFont = Output.getFont(null, -1, 22, gameOverScoreLabel.getFont());
        if (scoreFont != null)
            gameOverScoreLabel.setFont(scoreFont);
        buttonPanel.add(gameOverScoreLabel, BorderLayout.NORTH);

        JPanel spp = new JPanel();
        spp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(spp, BorderLayout.CENTER);

        JButton retryButton = new JButton();
        if (buttonFont != null)
            retryButton.setFont(buttonFont);
        retryButton.setText("Play again");
        retryButton.addActionListener(e -> {
            output.setToSingleplayerInGame();
            tetris.startSingleplayerGame();
        });
        spp.add(retryButton);

        JPanel spacer = new JPanel();
        spacer.setLayout(new BorderLayout(0, 30));
        buttonPanel.add(spacer, BorderLayout.SOUTH);
        JPanel p = new JPanel();
        spacer.add(p, BorderLayout.NORTH);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        topPanel.add(backPanel, BorderLayout.WEST);

        JButton backButton = new JButton();
        backButton.setText("< back");
        backButton.addActionListener(e -> {
            output.setToMainMenu();
        });
        backPanel.add(backButton);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }

    protected void setScore(int score) {
        gameOverScoreLabel.setText("Score: " + score);
    }
}
