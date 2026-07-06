public interface MatrixChainSolver {

    MatrixChainResult solve(int[] dims);

    default String optimalOrder(int[][] split, int i, int j) {
        if (i + 1 == j) {
            return "M" + j;
        }
        int k = split[i][j];
        return "(" + optimalOrder(split, i, k) + " x " + optimalOrder(split, k, j) + ")";
    }
}
