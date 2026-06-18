package matrixchain;

final class MatrixChainResult {
    final long minCost;
    final String parenthesization;

    MatrixChainResult(long minCost, String parenthesization) {
        this.minCost = minCost;
        this.parenthesization = parenthesization;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MatrixChainResult)) return false;
        MatrixChainResult other = (MatrixChainResult) obj;
        return minCost == other.minCost && parenthesization.equals(other.parenthesization);
    }

    @Override
    public int hashCode() {
        int h = (int) (minCost ^ (minCost >>> 32));
        return 31 * h + parenthesization.hashCode();
    }

    @Override
    public String toString() {
        return "Minimum multiplications: " + minCost + "\nOptimal order: " + parenthesization;
    }
}
