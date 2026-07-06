package matrixchain;

import java.util.Comparator;
import java.util.stream.IntStream;

public class DpMatrixChainSolver extends AbstractMatrixChainSolver {

    @Override
    protected int computeMinCost() {
        int[][] cost = new int[n][n];

        IntStream.range(2, n).forEach(len ->
            IntStream.range(0, n - len).forEach(i -> {
                int j = i + len;
                int k = IntStream.range(i + 1, j)
                    .boxed()
                    .min(Comparator.comparingInt(m -> cost[i][m] + cost[m][j] + multiplicationCost(i, m, j)))
                    .orElseThrow();
                cost[i][j] = cost[i][k] + cost[k][j] + multiplicationCost(i, k, j);
                split[i][j] = k;
            })
        );

        return cost[0][n - 1];
    }
}
