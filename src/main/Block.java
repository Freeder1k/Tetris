package main;

import java.awt.*;
import java.util.Arrays;

public class Block {//TODO board size or board
    private final int[][] points;
    private final int bBoxWidth;
    private final Color color;
    private int yPos;
    private int xPos;
    private int rotation;

    private Block(int[][] points, int bBoxWidth, Color color) {
        yPos = Tetris.BOARD_HEIGHT - bBoxWidth;
        xPos = (Tetris.BOARD_WIDTH - bBoxWidth) / 2;
        rotation = 0;
        this.points = points;
        this.bBoxWidth = bBoxWidth;
        this.color = color;
    }

    public static Block createSBlock() {
        return new Block(new int[][]{{0, 1}, {1, 1}, {1, 2}, {2, 2}}, 3, Color.GREEN);
    }

    public static Block createZBlock() {
        return new Block(new int[][]{{0, 2}, {1, 2}, {1, 1}, {2, 1}}, 3, Color.RED);
    }

    public static Block createCubeBlock() {
        return new Block(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, 2, Color.YELLOW);
    }

    public static Block createLBlock() {
        return new Block(new int[][]{{0, 1}, {1, 1}, {2, 1}, {2, 2}}, 3, Color.ORANGE);
    }

    public static Block createJBlock() {
        return new Block(new int[][]{{0, 1}, {0, 2}, {1, 1}, {2, 1}}, 3, Color.BLUE);
    }

    public static Block createIBlock() {
        return new Block(new int[][]{{0, 2}, {1, 2}, {2, 2}, {3, 2}}, 4, Color.CYAN);
    }

    public static Block createTBlock() {
        return new Block(new int[][]{{0, 1}, {1, 1}, {1, 2}, {2, 1}}, 3, Color.MAGENTA);
    }

    public synchronized boolean rotate(Color[][] board) {
        if (rotation == 3)
            rotation = 0;
        else
            rotation++;

        for (int[] point : points) {
            int xNew = bBoxWidth - 1 - point[0];
            int yNew = point[1];
            point[0] = yNew;
            point[1] = xNew;
        }

        //TODO wallkicks https://tetris.fandom.com/wiki/SRS

        return true;
    }

    public synchronized boolean moveDown(Color[][] board) {
        yPos--;
        if (overlaps(board)) {
            yPos++;
            return false;
        }
        return true;
    }

    public synchronized boolean moveRight(Color[][] board) {
        xPos++;
        if (overlaps(board)) {
            xPos--;
            return false;
        }
        return true;
    }

    public synchronized boolean moveLeft(Color[][] board) {
        xPos--;
        if (overlaps(board)) {
            xPos++;
            return false;
        }
        return true;
    }

    private boolean overlaps(Color[][] board) {
        for (int[] point : points) {
            int y = yPos + point[1];
            int x = xPos + point[0];

            if (y < 0 || x < 0 || x >= Tetris.BOARD_WIDTH)
                return true;

            if (y < Tetris.BOARD_HEIGHT) {
                if (board[y][x] != Color.BLACK) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized Color[][] getOverlay() {
        Color[][] overlay = new Color[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH];

        for (Color[] row : overlay) {
            Arrays.fill(row, Color.BLACK);
        }

        for (int[] point : points) {
            int y = yPos + point[1];
            if (y < Tetris.BOARD_HEIGHT)
                overlay[y][xPos + point[0]] = color;
        }

        return overlay;
    }

    public synchronized boolean isOnTop() {
        for (int[] point : points) {
            int y = yPos + point[1];

            if (y < Tetris.BOARD_HEIGHT - 2)
                return false;
        }
        return true;
    }

    public synchronized boolean canMoveDown(Color[][] board) {
        for (int[] point : points) {
            int y = yPos + point[1];

            if (y < 0)
                return false;

            if (y < Tetris.BOARD_HEIGHT) {
                if (board[y][xPos + point[0]] != Color.BLACK) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void addToBoard(Color[][] board) {
        for (int[] point : points) {
            int y = yPos + point[1];
            if (y < Tetris.BOARD_HEIGHT)
                board[yPos + point[1]][xPos + point[0]] = color;
        }
    }
}
