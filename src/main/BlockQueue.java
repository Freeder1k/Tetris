package main;

import java.util.ArrayDeque;
import java.util.Random;

public class BlockQueue {
    public final int seed;
    private final ArrayDeque<Block> queue;
    private final Random gen;

    public BlockQueue(int seed) {
        gen = new Random(seed);
        queue = new ArrayDeque<>(7);
        this.seed = seed;
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
     * Add a new random block to the queue. And remove the active one.
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
                return Block.createSBlock();
            case 1:
                return Block.createZBlock();
            case 2:
                return Block.createCubeBlock();
            case 3:
                return Block.createLBlock();
            case 4:
                return Block.createJBlock();
            case 5:
                return Block.createIBlock();
            case 6:
                return Block.createTBlock();
            default:
                return null;
        }
    }
}
