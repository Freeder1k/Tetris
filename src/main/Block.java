package main;

import java.util.Arrays;

public class Block {
    private final int[][] points;
    private final float[] midPoint;
    private int yPos;
    private int xPos;
    private int rotation;

    private Block(int[][] points, float[] midPoint) {
        yPos = 0;
        xPos = Tetris.BOARD_WIDTH / 2;
        rotation = 0;
        this.points = points;
        this.midPoint = midPoint;
    }

    //TODO random rotation
    public static Block createSBlock() {
        return new Block(new int[][]{{-1, 1}, {-1, 0}, {0, 0}, {0, -1}}, new float[]{0.0f, 0.0f});
    }

    public static Block createZBlock() {
        return new Block(new int[][]{{1, 1}, {1, 0}, {0, 0}, {0, -1}}, new float[]{0.0f, 0.0f});
    }

    public static Block createCubeBlock() {
        return new Block(new int[][]{{1, 1}, {1, 0}, {0, 0}, {0, 1}}, new float[]{0.5f, 0.5f});
    }

    public static Block createLBlock() {
        return new Block(new int[][]{{1, 1}, {1, 0}, {0, 0}, {-1, 0}}, new float[]{0.0f, 0.0f});
    }

    public static Block createJBlock() {
        return new Block(new int[][]{{1, 1}, {1, 0}, {0, 0}, {-1, 0}}, new float[]{0.0f, 0.0f});
    }

    public static Block createIBlock() {
        return new Block(new int[][]{{-1, 0}, {1, 0}, {0, 0}, {2, 0}}, new float[]{0.5f, 0.5f});
    }

    public static Block createTBlock() {
        return new Block(new int[][]{{0, 1}, {1, 0}, {0, 0}, {-1, 0}}, new float[]{0.0f, 0.0f});
    }

    public synchronized boolean rotate(boolean[][] board) {
        if (rotation == 3)
            rotation = 0;
        else
            rotation++;

        for (int[] point : points) {
            int xNew = Math.round(-((float) point[0] - midPoint[0]) + midPoint[1]);
            int yNew = Math.round(((float) point[1] - midPoint[1]) + midPoint[0]);
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
        boolean[][] overlay = getOverlay();

        for (int[] point : points) {
            int y = yPos - point[1];
            int x = xPos + point[0];

            if (y < 0 || y >= Tetris.BOARD_HEIGHT || x < 0 || x >= Tetris.BOARD_WIDTH)
                return true;

            if (overlay[y][x])
                return true;
        }
        return false;
    }

    public synchronized boolean[][] getOverlay() {
        boolean[][] overlay = new boolean[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH];

        for (boolean[] row : overlay) {
            Arrays.fill(row, false);
        }

        for (int[] point : points) {
            overlay[yPos - point[1]][xPos + point[0]] = true;
        }

        return overlay;
    }

    public synchronized void addToBoard(boolean[][] board) {
        for (int[] point : points) {
            board[yPos - point[1]][xPos + point[0]] = true;
        }
    }
}
