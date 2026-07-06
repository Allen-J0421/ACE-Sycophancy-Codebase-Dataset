package matrixchain;

import java.util.List;

public class MatrixChainMultiplication {

    private final MatrixChainSolver solver;

    public MatrixChainMultiplication(MatrixChainSolver solver) {
        this.solver = solver;
    }

    public void run(List<Matrix> matrices) {
        MatrixChainSolver.Result result = solver.solve(matrices);
        System.out.println(result.minCost());
    }

    public static void main(String[] args) {
        List<Matrix> matrices = List.of(
            new Matrix(2, 1),
            new Matrix(1, 3),
            new Matrix(3, 4)
        );
        new MatrixChainMultiplication(new MemoizedMatrixChainSolver()).run(matrices);
    }
}
