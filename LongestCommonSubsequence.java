import java.util.Objects;

public final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {
    }

    public static int lcs(String first, String second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        if (first.isEmpty() || second.isEmpty()) {
            return 0;
        }

        if (first.equals(second)) {
            return first.length();
        }

        return new LcsSolver(first, second).solve();
    }
}
