package matrixchain;

import java.util.function.IntFunction;

public class MemoizedMatrixChainSolver extends AbstractMatrixChainSolver {

    private final IntFunction<CacheStrategy> cacheFactory;

    public MemoizedMatrixChainSolver(IntFunction<CacheStrategy> cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public MemoizedMatrixChainSolver() {
        this(SubproblemMemo::new);
    }

    @Override
    protected int computeMinCost() {
        CacheStrategy cache = cacheFactory.apply(n);
        return recurse(cache, 0, n - 1);
    }

    private int recurse(CacheStrategy cache, int i, int j) {
        if (i + 1 >= j) return 0;
        return cache.computeIfAbsent(i, j, (ii, jj) -> {
            int minCost = Integer.MAX_VALUE;
            for (int k = ii + 1; k < jj; k++) {
                int c = recurse(cache, ii, k)
                      + recurse(cache, k, jj)
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
