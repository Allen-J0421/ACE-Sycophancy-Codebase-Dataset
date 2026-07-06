public class MatrixChainMultiplication {

    private final MatrixChainSolver solver;

    public MatrixChainMultiplication(MatrixChainSolver solver) {
        this.solver = solver;
    }

    public void run(int[] dims) {
        MatrixChainResult result = solver.solve(dims);
        System.out.println(result.minCost);
    }

    public static void main(String[] args) {
        int[] dims = { 2, 1, 3, 4 };
        new MatrixChainMultiplication(new DpMatrixChainSolver()).run(dims);
    }
}
