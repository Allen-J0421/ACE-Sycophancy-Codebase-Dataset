public final class MatrixChainResult {
    private static final MatrixChainResult EMPTY = new MatrixChainResult(0, new int[0][0], 0);

    private final int minimumCost;
    private final int[][] bestSplits;
    private final int matrixCount;

    static MatrixChainResult empty() {
        return EMPTY;
    }

    MatrixChainResult(int minimumCost, int[][] bestSplits, int matrixCount) {
        this.minimumCost = minimumCost;
        this.bestSplits = bestSplits;
        this.matrixCount = matrixCount;
    }

    public int minimumCost() {
        return minimumCost;
    }

    public String optimalParenthesization() {
        if (matrixCount <= 1) {
            return "A1";
        }

        return buildParenthesization(0, matrixCount);
    }

    private String buildParenthesization(int start, int end) {
        if (end - start == 1) {
            return "A" + (start + 1);
        }

        int split = bestSplits[start][end];
        return "("
            + buildParenthesization(start, split)
            + " x "
            + buildParenthesization(split, end)
            + ")";
    }
}
