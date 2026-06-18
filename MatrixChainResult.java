public final class MatrixChainResult {

    private final long minimumCost;
    private final String parenthesization;

    MatrixChainResult(long minimumCost, String parenthesization) {
        this.minimumCost = minimumCost;
        this.parenthesization = parenthesization;
    }

    public long minimumCost() {
        return minimumCost;
    }

    public String parenthesization() {
        return parenthesization;
    }
}
