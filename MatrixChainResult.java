public class MatrixChainResult {
    public final int minCost;
    public final int[][] splitTable;

    public MatrixChainResult(int minCost, int[][] splitTable) {
        this.minCost = minCost;
        this.splitTable = splitTable;
    }
}
