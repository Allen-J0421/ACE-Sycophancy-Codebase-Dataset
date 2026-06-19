package matrixchain;

/**
 * Outcome of solving a matrix-chain ordering problem: the minimum number of
 * scalar multiplications and a parenthesization that achieves it.
 */
final class MatrixChainResult {

    private final long minimumCost;
    private final String parenthesization;

    MatrixChainResult(long minimumCost, String parenthesization) {
        this.minimumCost = minimumCost;
        this.parenthesization = parenthesization;
    }

    /** Minimum scalar multiplications needed to evaluate the chain. */
    long minimumCost() {
        return minimumCost;
    }

    /** Optimal multiplication order, e.g. {@code (M1 x (M2 x M3))}. */
    String parenthesization() {
        return parenthesization;
    }

    @Override
    public String toString() {
        return parenthesization + " requires " + minimumCost + " scalar multiplications";
    }
}
