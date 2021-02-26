package main;

import java.util.ArrayDeque;
import java.util.Random;

public class BlockQueue {
    private final ArrayDeque<Block> queue;

    private final Random gen;

    public BlockQueue(int seed) {
        gen = new Random(seed);
        queue = new ArrayDeque<>(7);
        for (int i = 0; i < 6; i++) {
            queue.add(newBlock());
        }
    }

    public synchronized Block getActive() {
        return queue.peek();
    }

    /**
     * Add a new random block to the queue. And remove the active one.
     *
     * @return The removed block.
     */
    public synchronized Block nextBlock() {
        queue.add(newBlock());
        return queue.remove();
    }

    private Block newBlock() {
        return switch (gen.nextInt(7)) {
            case 0 -> Block.createSBlock();
            case 1 -> Block.createZBlock();
            case 2 -> Block.createCubeBlock();
            case 3 -> Block.createLBlock();
            case 4 -> Block.createJBlock();
            case 5 -> Block.createIBlock();
            case 6 -> Block.createTBlock();
            default -> null;
        };
    }
}
