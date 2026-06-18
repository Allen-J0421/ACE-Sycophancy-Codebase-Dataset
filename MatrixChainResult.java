public record MatrixChainResult(int minimumCost, String optimalParenthesization) {

    private static final MatrixChainResult EMPTY = new MatrixChainResult(0, "A1");

    static MatrixChainResult empty() {
        return EMPTY;
    }
}
