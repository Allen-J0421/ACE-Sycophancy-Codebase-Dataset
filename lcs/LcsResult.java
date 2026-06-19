package lcs;

import java.util.Objects;

public record LcsResult(int length, String subsequence) {
    public LcsResult {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }
        Objects.requireNonNull(subsequence, "subsequence");
    }
}
