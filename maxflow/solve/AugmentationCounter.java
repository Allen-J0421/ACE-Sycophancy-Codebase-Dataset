package maxflow.solve;

import maxflow.path.AugmentingPath;

/**
 * A {@link SolveListener} that counts how many augmenting paths a solver discovers.
 * This is the primary instrument for comparing the efficiency of path-finding
 * strategies: the same network solved with different {@link maxflow.path.AugmentingPathFinder}s
 * generally needs a different number of augmentations.
 *
 * <p>A counter accumulates across every solve it observes; use a fresh instance per run
 * to measure a single solve. Not thread-safe.
 */
public final class AugmentationCounter implements SolveListener {

    private long count;

    @Override
    public void onAugmentingPath(AugmentingPath path) {
        count++;
    }

    /** Returns the number of augmenting paths observed so far. */
    public long count() {
        return count;
    }

    @Override
    public String toString() {
        return count + " augmentation(s)";
    }
}
