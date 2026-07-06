package matrixchain;

import java.util.function.IntBinaryOperator;

public class SubproblemMemo implements CacheStrategy {

    private final Integer[][] cache;

    public SubproblemMemo(int n) {
        cache = new Integer[n][n];
    }

    @Override
    public int computeIfAbsent(int i, int j, IntBinaryOperator compute) {
        if (cache[i][j] == null) {
            cache[i][j] = compute.applyAsInt(i, j);
        }
        return cache[i][j];
    }
}
