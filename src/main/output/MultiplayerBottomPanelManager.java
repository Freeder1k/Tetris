package main.output;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MultiplayerBottomPanelManager {
    Set<JLabel> infoLabels = new HashSet<>();
    Set<JLabel> idLabels = new HashSet<>();
    private String info = "<html>Host name: unknown<br/>Port: 0</html>";
    private int id = 0;

    protected MultiplayerBottomPanelManager() {
    }

    protected JPanel create() {
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.add(infoPanel, BorderLayout.WEST);

        JLabel infoLabel = new JLabel();
        Font infoFont = Output.getFont(null, -1, 16, infoLabel.getFont());
        if (infoFont != null)
            infoLabel.setFont(infoFont);
        infoLabel.setText(info);
        infoPanel.add(infoLabel);

        infoLabels.add(infoLabel);

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BorderLayout());
        panel.add(idPanel, BorderLayout.EAST);

        JPanel idPanel2 = new JPanel();
        idPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        idPanel.add(idPanel2, BorderLayout.SOUTH);

        JLabel idLabel = new JLabel();
        if (infoFont != null)
            idLabel.setFont(infoFont);
        idLabel.setHorizontalAlignment(11);
        idLabel.setText("ID: " + id);
        idLabel.setVerticalAlignment(3);
        idLabel.setVerticalTextPosition(3);
        idPanel2.add(idLabel);

        idLabels.add(idLabel);

        return panel;
    }

    protected synchronized void setInfo(String hostName, int port) {
        info = "<html>Host name: " + hostName + "<br/>Port: " + port + "</html>";

        infoLabels.forEach(l -> l.setText(info));
    }

    protected synchronized void setID(int id) {
        this.id = id;
        idLabels.forEach(l -> l.setText("ID: " + id));
    }
}
