package matrixchain;

import java.util.function.IntBinaryOperator;

public class SubproblemMemo<V> {

    private final Object[][] cache;

    public SubproblemMemo(int n) {
        cache = new Object[n][n];
    }

    public boolean contains(int i, int j) {
        return cache[i][j] != null;
    }

    @SuppressWarnings("unchecked")
    public V get(int i, int j) {
        return (V) cache[i][j];
    }

    public V computeIfAbsent(int i, int j, IntBinaryOperator compute) {
        if (!contains(i, j)) {
            cache[i][j] = compute.applyAsInt(i, j);
        }
        return get(i, j);
    }
}
