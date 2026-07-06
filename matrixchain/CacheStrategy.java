package matrixchain;

import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface CacheStrategy {
    int computeIfAbsent(int i, int j, IntBinaryOperator compute);
}
