package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class InputListener implements KeyListener, WindowListener {
    private final Tetris tetris;

    public InputListener(Tetris tetris) {
        this.tetris = tetris;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 32:
                tetris.drop();       //space
                break;
            case 37:
                tetris.moveLeft();   //left
                break;
            case 38:
                tetris.rotate();     //up
                break;
            case 39:
                tetris.moveRight();  //right
                break;
            case 40:
                tetris.moveDown();   //down
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
    }

    @Override
    public void windowClosed(WindowEvent e) {
        tetris.stop();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        tetris.pause();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        tetris.resume();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        tetris.resume();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        tetris.pause();
    }
}
