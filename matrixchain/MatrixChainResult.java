package matrixchain;

final class MatrixChainResult {
    final int minCost;
    final String parenthesization;

    MatrixChainResult(int minCost, String parenthesization) {
        this.minCost = minCost;
        this.parenthesization = parenthesization;
    }

    @Override
    public String toString() {
        return "Minimum multiplications: " + minCost + "\nOptimal order: " + parenthesization;
    }
}
