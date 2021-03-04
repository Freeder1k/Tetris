package main.output;

import main.Tetris;

import javax.swing.*;
import java.awt.*;

public class MultiplayerMenu extends JPanel {
    protected MultiplayerMenu(Output output, Tetris tetris, Font titleFont, Font buttonFont, JPanel colorOptionPanel) {
        this.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel("<html><center>MULTI<br/>PLAYER</center></html>");
        if (titleFont != null)
            gameOverLabel.setFont(titleFont);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        this.add(gameOverLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        this.add(buttonPanel, BorderLayout.SOUTH);

        JPanel jbp = new JPanel();
        jbp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(jbp, BorderLayout.NORTH);

        JButton joinButton = new JButton();
        if (buttonFont != null)
            joinButton.setFont(buttonFont);
        joinButton.setText("Join game");
        joinButton.addActionListener(e -> output.setToMultiplayerJoin());
        jbp.add(joinButton);

        JPanel hbp = new JPanel();
        hbp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(hbp, BorderLayout.CENTER);

        JButton hostButton = new JButton();
        if (buttonFont != null)
            hostButton.setFont(buttonFont);
        hostButton.setText("Host game");
        hostButton.addActionListener(e -> tetris.hostMultiplayerGame());
        hbp.add(hostButton);

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
        backButton.addActionListener(e -> output.setToMainMenu());
        backPanel.add(backButton);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }
}
