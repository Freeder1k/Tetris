package main.output;

import main.Tetris;

import javax.swing.*;
import java.awt.*;

public class MultiplayerGameOver extends JPanel {
    private final JLabel infoLabel;

    protected MultiplayerGameOver(Output output, Tetris tetris, Font titleFont, Font buttonFont, Font labelFont, JPanel colorOptionPanel, JPanel multiplayerBottomPanel) {
        this.setLayout(new BorderLayout());

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BorderLayout());
        this.add(midPanel, BorderLayout.CENTER);

        JLabel gameOverLabel = new JLabel("<html><center>MULTI<br/>PLAYER</center></html>");
        if (titleFont != null)
            gameOverLabel.setFont(titleFont);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        midPanel.add(gameOverLabel, BorderLayout.CENTER);

        infoLabel = new JLabel();
        if (labelFont != null)
            infoLabel.setFont(labelFont);
        infoLabel.setText("<html><center>Waiting for all players to finish...<br/>Your rank: 1</center></html>");
        infoLabel.setHorizontalAlignment(0);
        midPanel.add(infoLabel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        this.add(bottomPanel, BorderLayout.SOUTH);

        JPanel spacer = new JPanel();
        spacer.setLayout(new BorderLayout(0, 50));
        bottomPanel.add(spacer, BorderLayout.CENTER);
        JPanel p = new JPanel();
        spacer.add(p, BorderLayout.NORTH);

        bottomPanel.add(multiplayerBottomPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        topPanel.add(backPanel, BorderLayout.WEST);

        JButton backButton = new JButton();
        backButton.setText("< back");
        backButton.addActionListener(e -> {
            tetris.leaveMultiplayerGame();
            output.setToMultiplayerMenu();
        });
        backPanel.add(backButton);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }

    protected void setInfo(int score, int rank) {//TODO score
        infoLabel.setText("<html><center>Waiting for all players to finish...<br/>Your rank: " + rank + "</center></html>");
    }
}
