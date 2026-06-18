package matrixchain;

import java.util.Objects;

public record MatrixChainResult(long minimumCost, String parenthesization) {

    public MatrixChainResult {
        Objects.requireNonNull(parenthesization, "parenthesization");
    }
}
