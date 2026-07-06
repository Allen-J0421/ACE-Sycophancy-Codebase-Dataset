package matrixchain;

import java.util.List;

public interface MatrixChainSolver {

    record Result(int minCost, SplitTable splitTable) {}

    Result solve(List<Matrix> matrices);
}
