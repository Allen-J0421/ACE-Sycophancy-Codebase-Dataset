package utils;

/**
 * Interface for BFS Callback
 *
 */
public interface BfsCallable {
    /**
     * Process each Block visited during the BFS search
     * @param row location
     * @param col location
     * @param temperature mean temperature to set the block to
     * @param count number of blocks processed already
     * @param blocks the total number of blocks to process
     * @param fade whether the temperature should fade as it gets further from the center
     */
    void execute(int row, int col, int temperature, int count, int blocks, boolean fade);
}
