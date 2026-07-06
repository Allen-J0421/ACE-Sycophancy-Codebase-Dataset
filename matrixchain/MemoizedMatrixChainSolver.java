package matrixchain;

public class MemoizedMatrixChainSolver extends AbstractMatrixChainSolver {

    @Override
    protected int computeMinCost() {
        SubproblemMemo<Integer> memo = new SubproblemMemo<>(n);
        return recurse(memo, 0, n - 1);
    }

    private int recurse(SubproblemMemo<Integer> memo, int i, int j) {
        if (i + 1 >= j) return 0;
        return memo.computeIfAbsent(i, j, (ii, jj) -> {
            int minCost = Integer.MAX_VALUE;
            for (int k = ii + 1; k < jj; k++) {
                int c = recurse(memo, ii, k)
                      + recurse(memo, k, jj)
                      + multiplicationCost(ii, k, jj);
                if (c < minCost) {
                    minCost = c;
                    split[ii][jj] = k;
                }
            }
            return minCost;
        });
    }
}
