import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Dimension;

public class Output extends JFrame {
    private JLabel label;
    private JTextField tf;
    
    private JLabel scoreLabel;
    private JPanel gamePanel;
    
    private boolean[][] board;
    private JPanel[][] gameDisplay = new JPanel[20][10];
    
    public Output(boolean[][] board) {
        this.board = board;
        this.setSize(350, 700);
        
        this.setTitle("Tetris");
        JPanel panel = new JPanel();

        panel.setLayout(new java.awt.BorderLayout());

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        gamePanel = new JPanel();
        
        gamePanel.setLayout(new java.awt.GridLayout(20, 10));
        gamePanel.setPreferredSize(new Dimension(300, 600));
        gamePanel.setMaximumSize(gamePanel.getPreferredSize()); 
        gamePanel.setMinimumSize(gamePanel.getPreferredSize());
        
        Border border = BorderFactory.createLineBorder(Color.gray);
        for(int i1 = 0; i1 < 20; i1++) {
            for(int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2] = new JPanel();
                gameDisplay[i1][i2].setBorder(border);
                gameDisplay[i1][i2].setBackground(Color.black);
                gamePanel.add(gameDisplay[i1][i2]);
            }
        }

        JPanel midPanel = new JPanel();
        midPanel.add(gamePanel);

        panel.add(scoreLabel, java.awt.BorderLayout.PAGE_START);
        panel.add(midPanel, java.awt.BorderLayout.CENTER);

        this.add(panel);
        this.setVisible(true);
    }

    private void updateOutput(ActiveBlock activeBlock, int score) {
        //TODO active block
        for(int i1 = 0; i1 < 20; i1++) {
            for(int i2 = 0; i2 < 10; i2++) {
                gameDisplay[i1][i2].setBackground(board[i1][i2]? Color.white: Color.black);
            }
        }
        //TODO update score
        this.setVisible(true);
    }
}