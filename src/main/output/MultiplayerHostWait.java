package main.output;

import main.Tetris;

import javax.swing.*;
import java.awt.*;

public class MultiplayerHostWait extends JPanel {
    private final JLabel playerLabel;

    protected MultiplayerHostWait(Output output, Tetris tetris, Font titleFont, Font buttonFont, Font labelFont, JPanel colorOptionPanel, JPanel multiplayerBottomPanel) {
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

        playerLabel = new JLabel();
        if (labelFont != null)
            playerLabel.setFont(labelFont);
        playerLabel.setText("Players: 1");
        playerLabel.setHorizontalAlignment(0);
        midPanel.add(playerLabel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        this.add(bottomPanel, BorderLayout.SOUTH);

        JPanel sbp = new JPanel();
        sbp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        bottomPanel.add(sbp, BorderLayout.CENTER);

        JButton startButton = new JButton();
        if (buttonFont != null)
            startButton.setFont(buttonFont);
        startButton.setText("Start");
        startButton.addActionListener(e -> {
            output. setToMultiplayerInGame();
            tetris.startMultiplayerGameAsHost();
            //TODO start game, set to multip in game
        });
        sbp.add(startButton);

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
            //TODO send leave
        });
        backPanel.add(backButton);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }

    protected void setPlayerCount(int amount) {
        playerLabel.setText("Players: " + amount);
    }
}
