package main;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Random;

public class BlockQueue {//TODO board size for blocks or board
    public final int seed;
    private final ArrayDeque<Block> queue;
    private final Random gen;
    private final Color[][] board;

    public BlockQueue(int seed, Color[][] board) {
        gen = new Random(seed);
        queue = new ArrayDeque<>(7);
        this.seed = seed;
        this.board = board;
        for (int i = 0; i < 6; i++) {
            Block newB = newBlock();
            if (newB != null)
                queue.add(newB);
        }
    }

    public synchronized Block getActive() {
        return queue.peek();
    }

    /**
     * Add a new random block to the queue and remove the active one.
     *
     * @return The removed block.
     */
    public synchronized Block nextBlock() {
        Block newB = newBlock();
        if (newB != null)
            queue.add(newB);
        return queue.remove();
    }

    private Block newBlock() {
        switch (gen.nextInt(7)) {
            case 0:
                return Block.createSBlock(board);
            case 1:
                return Block.createZBlock(board);
            case 2:
                return Block.createCubeBlock(board);
            case 3:
                return Block.createLBlock(board);
            case 4:
                return Block.createJBlock(board);
            case 5:
                return Block.createIBlock(board);
            case 6:
                return Block.createTBlock(board);
            default:
                return null;
        }
    }
}
