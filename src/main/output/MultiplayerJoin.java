package main.output;

import javax.swing.*;
import java.awt.*;

public class MultiplayerJoin extends JPanel {
    protected MultiplayerJoin(Output output, Font titleFont, Font buttonFont, JPanel colorOptionPanel) {
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

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BorderLayout(0, 0));
        buttonPanel.add(entryPanel, BorderLayout.NORTH);

        JPanel hostNamePanel = new JPanel();
        hostNamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        entryPanel.add(hostNamePanel, BorderLayout.CENTER);

        JLabel hostNameLabel = new JLabel();
        Font labelFont = Output.getFont(null, -1, 20, hostNameLabel.getFont());
        if (labelFont != null)
            hostNameLabel.setFont(labelFont);
        hostNameLabel.setText("Host name:");
        hostNamePanel.add(hostNameLabel);

        JTextField hostNameTextField = new JTextField();
        hostNameTextField.setColumns(10);
        if (labelFont != null)
            hostNameTextField.setFont(labelFont);
        hostNameTextField.setText("");
        hostNameTextField.setToolTipText("Host name");
        hostNamePanel.add(hostNameTextField);

        JPanel portPanel = new JPanel();
        portPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        entryPanel.add(portPanel, BorderLayout.SOUTH);

        JLabel portLabel = new JLabel();
        if (labelFont != null)
            portLabel.setFont(labelFont);
        portLabel.setText("          Port:");
        portPanel.add(portLabel);

        JTextField portTextField = new JTextField();
        portTextField.setColumns(10);
        if (labelFont != null)
            portTextField.setFont(labelFont);
        portTextField.setText("");
        portTextField.setToolTipText("Port");
        portPanel.add(portTextField);

        JPanel jbp = new JPanel();
        jbp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(jbp, BorderLayout.CENTER);

        JButton joinButton = new JButton();
        if (buttonFont != null)
            joinButton.setFont(buttonFont);
        joinButton.setText("Join");
        joinButton.addActionListener(e -> {
            //TODO check data and join if can and then set to multip client waiting
            output.setToMultiplayerClientWait("abc", 0, 0, 1);
        });
        jbp.add(joinButton);

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
            output.setToMultiplayerMenu();
        });
        backPanel.add(backButton);

        topPanel.add(colorOptionPanel, BorderLayout.EAST);
    }
}
