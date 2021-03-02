package main.output;

import javax.swing.*;
import java.awt.*;

public class MultiplayerGameOver extends JPanel {
    private final JLabel playerLabel;

    protected MultiplayerGameOver(Output output, Font titleFont, Font buttonFont, Font labelFont, JPanel colorOptionPanel, JPanel multiplayerBottomPanel) {
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
        playerLabel.setText("<html><center>Waiting for all players to finish...<br/>Players left: 1</center></html>");
        playerLabel.setHorizontalAlignment(0);
        midPanel.add(playerLabel, BorderLayout.SOUTH);

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
            output.setToMultiplayerMenu();
            //TODO send leave
        });
        backPanel.add(backButton);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }

    protected void setPlayerCount(int amount) {
        playerLabel.setText("<html><center>Waiting for all players to finish...<br/>Players left: " + amount + "</center></html>");
    }

    protected void setInfo(int score, int rank) {
        //TODO
    }
}
