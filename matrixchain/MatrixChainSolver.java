package matrixchain;

public interface MatrixChainSolver {

    class Result {
        public final int minCost;
        public final int[][] splitTable;

        public Result(int minCost, int[][] splitTable) {
            this.minCost = minCost;
            this.splitTable = splitTable;
        }
    }

    Result solve(int[] dims);

    default String optimalOrder(int[][] split, int i, int j) {
        if (i + 1 == j) {
            return "M" + j;
        }
        int k = split[i][j];
        return "(" + optimalOrder(split, i, k) + " x " + optimalOrder(split, k, j) + ")";
    }
}
