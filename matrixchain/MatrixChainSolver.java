package matrixchain;

public interface MatrixChainSolver {

    record Result(int minCost, SplitTable splitTable) {}

    Result solve(int[] dims);
}
