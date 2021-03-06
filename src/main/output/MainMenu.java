package main.output;

import main.Tetris;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    protected MainMenu(Output output, Tetris tetris, Font titleFont, Font buttonFont, JPanel colorOptionPanel) {
        this.setLayout(new BorderLayout());

        JLabel tetrisLabel = new JLabel("TETRIS");
        if (titleFont != null)
            tetrisLabel.setFont(titleFont);
        tetrisLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tetrisLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        this.add(tetrisLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        this.add(buttonPanel, BorderLayout.SOUTH);

        JPanel spp = new JPanel();
        spp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(spp, BorderLayout.NORTH);

        JButton singleplayerButton = new JButton();
        if (buttonFont != null)
            singleplayerButton.setFont(buttonFont);
        singleplayerButton.setText("Singleplayer");
        singleplayerButton.addActionListener(e -> {
            output.setToSingleplayerInGame();
            tetris.startSinglePlayerGame();
        });
        spp.add(singleplayerButton);

        JPanel mpp = new JPanel();
        mpp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(mpp, BorderLayout.CENTER);

        JButton multiplayerButton = new JButton();
        if (buttonFont != null)
            multiplayerButton.setFont(buttonFont);
        multiplayerButton.setText("Multiplayer");
        multiplayerButton.addActionListener(e -> output.setToMultiplayerMenu());
        mpp.add(multiplayerButton);

        JPanel spacer = new JPanel();
        spacer.setLayout(new BorderLayout(0, 30));
        buttonPanel.add(spacer, BorderLayout.SOUTH);
        JPanel p = new JPanel();
        spacer.add(p, BorderLayout.NORTH);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }
}
