public class MatrixChainMultiplication {

    public static void main(String[] args) {
        int[] dims = { 2, 1, 3, 4 };

        MatrixChainSolver solver = new MatrixChainSolver();
        MatrixChainResult result = solver.solve(dims);

        System.out.println(result.minCost);
    }
}
