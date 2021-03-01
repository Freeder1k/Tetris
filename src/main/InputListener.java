package main;

import main.gameHandler.TetrisGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class InputListener implements KeyListener, WindowListener {
    private final Tetris tetris;
    private TetrisGame gameHandler;

    public InputListener(Tetris tetris) {
        this.tetris = tetris;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameHandler == null)
            return;
        switch (e.getKeyCode()) {
            case 32:
                gameHandler.drop();       //space
                break;
            case 37:
                gameHandler.moveLeft();   //left
                break;
            case 38:
                gameHandler.rotate();     //up
                break;
            case 39:
                gameHandler.moveRight();  //right
                break;
            case 40:
                gameHandler.moveDown();   //down
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //do nothing
    }


    @Override
    public void windowOpened(WindowEvent e) {
        //do nothing
    }

    @Override
    public void windowClosing(WindowEvent e) {
        tetris.stop();
        if(gameHandler != null)
            gameHandler.stop();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        tetris.stop();
        if(gameHandler != null)
            gameHandler.stop();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if(gameHandler != null)
            gameHandler.pause();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if(gameHandler != null)
            gameHandler.resume();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if(gameHandler != null)
            gameHandler.resume();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if(gameHandler != null)
            gameHandler.pause();
    }
    
    public void setGameHandler(TetrisGame gameHandler) {
        this.gameHandler = gameHandler;
    }
}
