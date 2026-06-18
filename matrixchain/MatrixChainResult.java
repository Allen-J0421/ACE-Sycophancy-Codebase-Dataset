package matrixchain;

final class MatrixChainResult {
    final long minCost;
    final String parenthesization;

    MatrixChainResult(long minCost, String parenthesization) {
        this.minCost = minCost;
        this.parenthesization = parenthesization;
    }

    @Override
    public String toString() {
        return "Minimum multiplications: " + minCost + "\nOptimal order: " + parenthesization;
    }
}
