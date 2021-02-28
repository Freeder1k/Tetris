package main.output;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashSet;
import java.util.Set;

public class ColorOptionPanelManager {
    private final Set<JComboBox<String>> comboboxes = new HashSet<>();
    private final Output output;
    private int selectedIndex = 0;

    protected ColorOptionPanelManager(Output output) {
        this.output = output;
    }

    protected JPanel create() {
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JPanel colorOptionPanel = new JPanel();
        colorOptionPanel.setLayout(new BorderLayout());
        basePanel.add(colorOptionPanel);

        JLabel colorLabel = new JLabel();
        colorLabel.setText("Color Mode:");
        colorOptionPanel.add(colorLabel, BorderLayout.NORTH);
        JComboBox<String> colorBox = new JComboBox<>();
        colorBox.addItem("Standard");
        colorBox.addItem("Blind Mode");
        colorBox.addItem("Rainbow Mode");
        colorBox.setToolTipText("Color");
        colorBox.setSelectedIndex(selectedIndex);
        colorBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                String item = (String) event.getItem();
                switch (item) {
                    case "Standard":
                        selectedIndex = 0;
                        break;
                    case "Blind Mode":
                        selectedIndex = 1;
                        break;
                    case "Rainbow Mode":
                        selectedIndex = 2;
                        break;
                }
                comboboxes.forEach(b -> b.setSelectedIndex(selectedIndex));
                //TODO output.updatecolormode
            }
        });
        colorOptionPanel.add(colorBox, BorderLayout.WEST);

        comboboxes.add(colorBox);

        return basePanel;
    }
}
