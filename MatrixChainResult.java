public final class MatrixChainResult {
    private static final MatrixChainResult EMPTY = new MatrixChainResult(0, "A1");

    private final int minimumCost;
    private final String optimalParenthesization;

    static MatrixChainResult empty() {
        return EMPTY;
    }

    MatrixChainResult(int minimumCost, String optimalParenthesization) {
        this.minimumCost = minimumCost;
        this.optimalParenthesization = optimalParenthesization;
    }

    public int minimumCost() {
        return minimumCost;
    }

    public String optimalParenthesization() {
        return optimalParenthesization;
    }
}
