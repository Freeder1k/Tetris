package main;

import java.awt.*;
import java.util.Arrays;

public class Block {
    private final int[][] points;
    private final int bBoxWidth;
    private final Color color;
    private final Color[][] board;
    private final int height;
    private final int width;
    private int yPos;
    private int xPos;
    private int rotation;

    private Block(int[][] points, int bBoxWidth, Color color, Color[][] board) {
        height = board.length;
        width = board[0].length;
        yPos = height - bBoxWidth;
        xPos = (width - bBoxWidth) / 2;
        rotation = 0;
        this.points = points;
        this.bBoxWidth = bBoxWidth;
        this.color = color;
        this.board = board;
    }

    public static Block createSBlock(Color[][] board) {
        return new Block(new int[][]{{0, 1}, {1, 1}, {1, 2}, {2, 2}}, 3, Color.GREEN, board);
    }

    public static Block createZBlock(Color[][] board) {
        return new Block(new int[][]{{0, 2}, {1, 2}, {1, 1}, {2, 1}}, 3, Color.RED, board);
    }

    public static Block createCubeBlock(Color[][] board) {
        return new Block(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, 2, Color.YELLOW, board);
    }

    public static Block createLBlock(Color[][] board) {
        return new Block(new int[][]{{0, 1}, {1, 1}, {2, 1}, {2, 2}}, 3, Color.ORANGE, board);
    }

    public static Block createJBlock(Color[][] board) {
        return new Block(new int[][]{{0, 1}, {0, 2}, {1, 1}, {2, 1}}, 3, Color.BLUE, board);
    }

    public static Block createIBlock(Color[][] board) {
        return new Block(new int[][]{{0, 2}, {1, 2}, {2, 2}, {3, 2}}, 4, Color.CYAN, board);
    }

    public static Block createTBlock(Color[][] board) {
        return new Block(new int[][]{{0, 1}, {1, 1}, {1, 2}, {2, 1}}, 3, Color.MAGENTA, board);
    }

    public synchronized boolean rotate() {
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

    public synchronized boolean moveDown() {
        yPos--;
        if (overlaps()) {
            yPos++;
            return false;
        }
        return true;
    }

    public synchronized boolean moveRight() {
        xPos++;
        if (overlaps()) {
            xPos--;
            return false;
        }
        return true;
    }

    public synchronized boolean moveLeft() {
        xPos--;
        if (overlaps()) {
            xPos++;
            return false;
        }
        return true;
    }

    public boolean overlaps() {
        for (int[] point : points) {
            int y = yPos + point[1];
            int x = xPos + point[0];

            if (y < 0 || x < 0 || x >= width)
                return true;

            if (y < height) {
                if (board[y][x] != Color.BLACK) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized Color[][] getOverlay() {
        Color[][] overlay = new Color[height][width];

        for (Color[] row : overlay) {
            Arrays.fill(row, Color.BLACK);
        }

        for (int[] point : points) {
            int y = yPos + point[1];
            if (y < height)
                overlay[y][xPos + point[0]] = color;
        }

        return overlay;
    }

    public synchronized boolean isOnTop() {
        for (int[] point : points) {
            int y = yPos + point[1];

            if (y < height - 2)
                return false;
        }
        return true;
    }

    public synchronized boolean canMoveDown() {
        for (int[] point : points) {
            int y = yPos + point[1];

            if (y < 0)
                return false;

            if (y < height) {
                if (board[y][xPos + point[0]] != Color.BLACK) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void addToBoard() {
        for (int[] point : points) {
            int y = yPos + point[1];
            if (y < height)
                board[yPos + point[1]][xPos + point[0]] = color;
        }
    }
}
