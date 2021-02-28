package main.output;

import main.BlockQueue;
import main.InputListener;
import main.Tetris;

import javax.swing.*;
import java.awt.*;

public class Output extends JFrame {
    private final InputListener inputListener;
    private final Tetris tetris;
    private final Container contentPane;
    private final MainMenu mainMenu;
    private final SingleplayerInGame singleplayerInGame;
    private final SingleplayerGameOver singleplayerGameOver;
    private final MultiplayerMenu multiplayerMenu;
    private final MultiplayerJoin multiplayerJoin;
    private final MultiplayerBottomPanelManager multiplayerBottomPanelManager;
    private final MultiplayerHostWait multiplayerHostWait;
    private final MultiplayerClientWait multiplayerClientWait;
    private final MultiplayerGameOver multiplayerGameOver;

    public Output(Color[][] board, InputListener inputListener, Tetris tetris) {
        this.inputListener = inputListener;
        this.tetris = tetris;

        this.setSize(Tetris.BOARD_WIDTH * 30 + 50, Tetris.BOARD_HEIGHT * 30 + 50);
        this.setResizable(false);

        this.setTitle("Tetris");

        contentPane = this.getContentPane();
        
        ColorOptionPanelManager colorOptionPanelManager = new ColorOptionPanelManager(this);
        multiplayerBottomPanelManager = new MultiplayerBottomPanelManager();

        Font titleFont = getFont("Rockwell Extra Bold", Font.BOLD, 72, new JLabel().getFont());
        //Font titleFont = new Font("Rockwell Extra Bold", Font.BOLD, 72);
        Font buttonFont = getFont(null, -1, 48, new JButton().getFont());
        //Font buttonFont = new Font(colorLabel.getFont().getName(), Font.PLAIN, 48);
        Font labelFont = getFont(null, -1, 16, new JLabel().getFont());
        

        mainMenu = new MainMenu(this, titleFont, buttonFont, colorOptionPanelManager.create());
        singleplayerInGame = new SingleplayerInGame(board);
        singleplayerGameOver = new SingleplayerGameOver(this, titleFont, buttonFont, colorOptionPanelManager.create());
        multiplayerMenu = new MultiplayerMenu(this, titleFont, buttonFont, colorOptionPanelManager.create());
        multiplayerJoin = new MultiplayerJoin(this, titleFont, buttonFont, colorOptionPanelManager.create());
        multiplayerHostWait = new MultiplayerHostWait(this, titleFont, buttonFont, labelFont, colorOptionPanelManager.create(), multiplayerBottomPanelManager.create());
        multiplayerClientWait = new MultiplayerClientWait(this, titleFont, buttonFont, labelFont, colorOptionPanelManager.create(), multiplayerBottomPanelManager.create());
        multiplayerGameOver = new MultiplayerGameOver(this, titleFont, buttonFont, labelFont, colorOptionPanelManager.create(), multiplayerBottomPanelManager.create());
        
        contentPane.add(mainMenu);

        this.addWindowListener(inputListener);
        this.setVisible(true);
    }

    protected static Font getFont(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    public synchronized void updateOutput(BlockQueue blockQueue, int score) {
        singleplayerInGame.update(blockQueue, score);
    }

    public synchronized void setToGameOverMenu(int score) {
        setToSingleplayerGameOver(score);//TODO remove
    }

    protected synchronized void setToMainMenu() {
        contentPane.removeAll();
        contentPane.repaint();
        
        contentPane.add(mainMenu);

        this.setVisible(true);
    }

    protected synchronized void setToSingleplayerInGame() {
        contentPane.removeAll();
        contentPane.repaint();
        
        singleplayerInGame.reset();

        contentPane.add(singleplayerInGame);
        
        contentPane.requestFocusInWindow();
        contentPane.addKeyListener(inputListener);

        this.setVisible(true);
    }

    public synchronized void setToSingleplayerGameOver(int score) {
        contentPane.removeAll();
        contentPane.repaint();
        contentPane.removeKeyListener(inputListener);

        singleplayerGameOver.setScore(score);
        contentPane.add(singleplayerGameOver);

        this.setVisible(true);
    }


    protected synchronized void setToMultiplayerMenu() {
        contentPane.removeAll();
        contentPane.repaint();

        contentPane.add(multiplayerMenu);

        this.setVisible(true);
    }
    
    protected synchronized void setToMultiplayerJoin() {
        contentPane.removeAll();
        contentPane.repaint();

        contentPane.add(multiplayerJoin);

        this.setVisible(true);
    }
    
    protected synchronized void setToMultiplayerHostWait(String hostName, int port) {
        contentPane.removeAll();
        contentPane.repaint();

        multiplayerBottomPanelManager.setInfo(hostName, port);
        multiplayerBottomPanelManager.setID(0);

        contentPane.add(multiplayerHostWait);

        this.setVisible(true);
    }
    
    protected synchronized void setToMultiplayerClientWait(String hostName, int port, int id, int playerCount) {
        contentPane.removeAll();
        contentPane.repaint();

        multiplayerBottomPanelManager.setInfo(hostName, port);
        multiplayerBottomPanelManager.setID(id);

        multiplayerClientWait.setPlayerCount(playerCount);

        contentPane.add(multiplayerClientWait);

        this.setVisible(true);
    }
    
    protected synchronized void setToMultiplayerGameOver(int playersLeft) {
        contentPane.removeAll();
        contentPane.repaint();

        multiplayerGameOver.setPlayerCount(playersLeft);

        contentPane.add(multiplayerGameOver);

        this.setVisible(true);
    }
    
    
    
    

    protected void startSingleplayerGame() {
        tetris.startSingleplayerGame();
    }

    public void setToMultiplayerGameOverMenu() {

    }

    public void startMultiplayerGame() {
    }
}