package matrixchain;

/**
 * Computes the optimal way to parenthesize a chain of matrix multiplications
 * using the classic O(n^3) time / O(n^2) space dynamic-programming algorithm.
 *
 * <p>The cost of multiplying an {@code a x b} matrix by a {@code b x c} matrix
 * is {@code a * b * c} scalar multiplications. Matrix multiplication is
 * associative, so the order of products does not change the result but can
 * change the cost dramatically; this solver finds the cheapest order.
 */
final class MatrixChainSolver {

    /**
     * Solves the chain described by {@code dimensions}.
     *
     * @return the minimum cost and the parenthesization that achieves it
     */
    MatrixChainResult solve(MatrixDimensions dimensions) {
        int matrices = dimensions.matrixCount();

        // cost[i][j]  = minimum multiplications to evaluate matrices i..j inclusive.
        // split[i][j] = index k of the optimal break: (i..k)(k+1..j).
        long[][] cost = new long[matrices][matrices];
        int[][] split = new int[matrices][matrices];

        for (int length = 2; length <= matrices; length++) {
            for (int i = 0; i + length - 1 < matrices; i++) {
                int j = i + length - 1;
                cost[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long candidate = cost[i][k] + cost[k + 1][j]
                        + (long) dimensions.at(i) * dimensions.at(k + 1) * dimensions.at(j + 1);
                    if (candidate < cost[i][j]) {
                        cost[i][j] = candidate;
                        split[i][j] = k;
                    }
                }
            }
        }

        String parenthesization = buildParenthesization(split, 0, matrices - 1);
        return new MatrixChainResult(cost[0][matrices - 1], parenthesization);
    }

    /** Recursively reconstructs the optimal order from the split table. */
    private String buildParenthesization(int[][] split, int i, int j) {
        if (i == j) {
            return "M" + (i + 1);
        }
        int k = split[i][j];
        return "(" + buildParenthesization(split, i, k)
            + " x " + buildParenthesization(split, k + 1, j) + ")";
    }
}
