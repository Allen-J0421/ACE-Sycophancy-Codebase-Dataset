package matrixchain;

import java.util.Comparator;
import java.util.stream.IntStream;

public class DpMatrixChainSolver implements MatrixChainSolver {

    @Override
    public Result solve(int[] dims) {
        int n = dims.length;
        int[][] cost = new int[n][n];
        int[][] split = new int[n][n];

        IntStream.range(2, n).forEach(len ->
            IntStream.range(0, n - len).forEach(i -> {
                int j = i + len;
                int k = IntStream.range(i + 1, j)
                    .boxed()
                    .min(Comparator.comparingInt(m -> cost[i][m] + cost[m][j] + dims[i] * dims[m] * dims[j]))
                    .orElseThrow();
                cost[i][j] = cost[i][k] + cost[k][j] + dims[i] * dims[k] * dims[j];
                split[i][j] = k;
            })
        );

        return new Result(cost[0][n - 1], split);
    }
}
