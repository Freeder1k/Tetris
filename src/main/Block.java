package main;

import java.util.Arrays;

public class Block {
    private final int[][] points;
    private final int bBoxWidth;
    private int yPos;
    private int xPos;
    private int rotation;

    private Block(int[][] points, int bBoxWidth) {
        yPos = Tetris.BOARD_HEIGHT - bBoxWidth;
        xPos = (Tetris.BOARD_WIDTH - bBoxWidth) / 2;
        rotation = 0;
        this.points = points;
        this.bBoxWidth = bBoxWidth;
    }

    public static Block createSBlock() {
        return new Block(new int[][]{{0, 1}, {1, 1}, {1, 2}, {2, 2}}, 3);
    }

    public static Block createZBlock() {
        return new Block(new int[][]{{0, 2}, {1, 2}, {1, 1}, {2, 1}}, 3);
    }

    public static Block createCubeBlock() {
        return new Block(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, 2);
    }

    public static Block createLBlock() {
        return new Block(new int[][]{{0, 1}, {1, 1}, {2, 1}, {2, 2}}, 3);
    }

    public static Block createJBlock() {
        return new Block(new int[][]{{0, 1}, {0, 2}, {1, 1}, {2, 1}}, 3);
    }

    public static Block createIBlock() {
        return new Block(new int[][]{{0, 2}, {1, 2}, {2, 2}, {3, 2}}, 4);
    }

    public static Block createTBlock() {
        return new Block(new int[][]{{0, 1}, {1, 1}, {1, 2}, {2, 1}}, 3);
    }

    public synchronized boolean rotate(boolean[][] board) {
        if (rotation == 3)
            rotation = 0;
        else
            rotation++;

        for (int[] point : points) {
            int xNew = Math.round(-(((float) point[0]) - ((float) (bBoxWidth - 1)) / 2.0f) + (((float) (bBoxWidth - 1))) / 2.0f);
            int yNew = Math.round((((float) point[1]) - ((float) (bBoxWidth - 1)) / 2.0f) + (((float) (bBoxWidth - 1))) / 2.0f);
            point[0] = yNew;
            point[1] = xNew;
        }

        //TODO wallkicks https://tetris.fandom.com/wiki/SRS

        return true;
    }

    public synchronized boolean moveDown(boolean[][] board) {
        yPos--;
        if (overlaps(board)) {
            yPos++;
            return false;
        }
        return true;
    }

    public synchronized boolean moveRight(boolean[][] board) {
        xPos++;
        if (overlaps(board)) {
            xPos--;
            return false;
        }
        return true;
    }

    public synchronized boolean moveLeft(boolean[][] board) {
        xPos--;
        if (overlaps(board)) {
            xPos++;
            return false;
        }
        return true;
    }

    private boolean overlaps(boolean[][] board) {
        for (int[] point : points) {
            int y = yPos + point[1];
            int x = xPos + point[0];

            if (y < 0 || x < 0 || x >= Tetris.BOARD_WIDTH)
                return true;

            if (y < Tetris.BOARD_HEIGHT) {
                if (board[y][x]) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized boolean[][] getOverlay() {
        boolean[][] overlay = new boolean[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH];

        for (boolean[] row : overlay) {
            Arrays.fill(row, false);
        }

        for (int[] point : points) {
            int y = yPos + point[1];
            if (y < Tetris.BOARD_HEIGHT)
                overlay[y][xPos + point[0]] = true;
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

    public synchronized void addToBoard(boolean[][] board) {
        for (int[] point : points) {
            int y = yPos + point[1];
            if (y < Tetris.BOARD_HEIGHT)
                board[yPos + point[1]][xPos + point[0]] = true;
        }
    }
}
