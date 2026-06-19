import java.util.Objects;
import java.util.function.Supplier;

/**
 * Longest Common Subsequence (LCS) of two strings.
 *
 * <p>A subsequence keeps the relative order of characters but need not be
 * contiguous. For example, the LCS of "AGGTAB" and "GXTXAYB" is "GTAB".
 *
 * <p>The dynamic-programming work is abstracted behind {@link LcsStrategy} so
 * that different algorithms can be plugged in without touching callers. Two
 * implementations are provided:
 * <ul>
 *   <li>{@link FullTableStrategy} — the classic O(m*n) table; simplest to read,
 *       reconstructs the subsequence by backtracking through the table.</li>
 *   <li>{@link SpaceOptimizedStrategy} — O(min(m, n)) space: rolling rows for
 *       the length and Hirschberg's divide-and-conquer for reconstruction.</li>
 * </ul>
 * Both strategies return identical results; they trade memory for code
 * complexity.
 */
final class LongestCommonSubsequence {

    /** Strategy for computing the LCS of two strings. */
    interface LcsStrategy {

        /**
         * Returns the length of the longest common subsequence of
         * {@code first} and {@code second}.
         *
         * @throws NullPointerException if either argument is null
         */
        int length(String first, String second);

        /**
         * Returns one longest common subsequence as a string. When several
         * subsequences share the maximum length, an arbitrary one is returned.
         *
         * @throws NullPointerException if either argument is null
         */
        String subsequence(String first, String second);
    }

    /**
     * Classic bottom-up DP that fills the full (m+1) x (n+1) table. Uses
     * O(m*n) space and reconstructs the subsequence by walking the table back
     * from the bottom-right corner.
     */
    static final class FullTableStrategy implements LcsStrategy {

        @Override
        public int length(String first, String second) {
            int[][] dp = table(first, second);
            return dp[first.length()][second.length()];
        }

        @Override
        public String subsequence(String first, String second) {
            int[][] dp = table(first, second);

            StringBuilder reversed = new StringBuilder();
            int i = first.length();
            int j = second.length();
            while (i > 0 && j > 0) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    reversed.append(first.charAt(i - 1));
                    i--;
                    j--;
                } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                    i--;
                } else {
                    j--;
                }
            }
            return reversed.reverse().toString();
        }

        /** Builds the full DP table of LCS lengths for all prefix pairs. */
        private static int[][] table(String first, String second) {
            Objects.requireNonNull(first, "first must not be null");
            Objects.requireNonNull(second, "second must not be null");

            int m = first.length();
            int n = second.length();
            int[][] dp = new int[m + 1][n + 1];

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (first.charAt(i - 1) == second.charAt(j - 1)) {
                        dp[i][j] = dp[i - 1][j - 1] + 1;
                    } else {
                        dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                    }
                }
            }
            return dp;
        }
    }

    /**
     * Linear-space DP. The length is computed by keeping only two rows; the
     * subsequence is reconstructed with Hirschberg's divide-and-conquer
     * algorithm, which also runs in O(min(m, n)) space (versus O(m*n) for the
     * full table). Time stays O(m*n).
     */
    static final class SpaceOptimizedStrategy implements LcsStrategy {

        @Override
        public int length(String first, String second) {
            Objects.requireNonNull(first, "first must not be null");
            Objects.requireNonNull(second, "second must not be null");
            int[] lastRow = lastRow(first, second);
            return lastRow[second.length()];
        }

        @Override
        public String subsequence(String first, String second) {
            Objects.requireNonNull(first, "first must not be null");
            Objects.requireNonNull(second, "second must not be null");
            return hirschberg(first, second);
        }

        /**
         * Returns the final row of the LCS table for {@code first} against
         * every prefix of {@code second}, i.e. {@code result[j]} is the LCS
         * length of {@code first} and {@code second.substring(0, j)}.
         */
        private static int[] lastRow(String first, String second) {
            int m = first.length();
            int n = second.length();
            int[] previous = new int[n + 1];
            int[] current = new int[n + 1];

            for (int i = 1; i <= m; i++) {
                current[0] = 0;
                for (int j = 1; j <= n; j++) {
                    if (first.charAt(i - 1) == second.charAt(j - 1)) {
                        current[j] = previous[j - 1] + 1;
                    } else {
                        current[j] = Math.max(previous[j], current[j - 1]);
                    }
                }
                int[] swap = previous;
                previous = current;
                current = swap;
            }
            return previous;
        }

        /** Hirschberg's linear-space reconstruction of one LCS. */
        private static String hirschberg(String first, String second) {
            int m = first.length();
            int n = second.length();

            if (m == 0 || n == 0) {
                return "";
            }
            if (m == 1) {
                return second.indexOf(first.charAt(0)) >= 0 ? first : "";
            }

            int mid = m / 2;
            String left = first.substring(0, mid);
            String right = first.substring(mid);

            // Score the left half forward and the right half backward, then
            // pick the split point in `second` that maximizes their sum.
            int[] scoreLeft = lastRow(left, second);
            int[] scoreRight = lastRow(reverse(right), reverse(second));

            int bestSplit = 0;
            int bestScore = -1;
            for (int k = 0; k <= n; k++) {
                int score = scoreLeft[k] + scoreRight[n - k];
                if (score > bestScore) {
                    bestScore = score;
                    bestSplit = k;
                }
            }

            return hirschberg(left, second.substring(0, bestSplit))
                    + hirschberg(right, second.substring(bestSplit));
        }

        private static String reverse(String s) {
            return new StringBuilder(s).reverse().toString();
        }
    }

    // ----- Strategy selection / configuration -----------------------------

    /**
     * System property that selects the default strategy used by the static
     * {@link #length} and {@link #subsequence} convenience methods, e.g.
     * {@code -Dlcs.strategy=space-optimized}. When unset, {@link
     * Strategy#FULL_TABLE} is used. An unrecognized value falls back to
     * {@code FULL_TABLE} with a warning on standard error; the {@link
     * #create(String)} factory, by contrast, rejects unknown ids outright.
     */
    static final String STRATEGY_PROPERTY = "lcs.strategy";

    /** The available strategies, each paired with a stable string id. */
    enum Strategy {
        FULL_TABLE("full-table", FullTableStrategy::new),
        SPACE_OPTIMIZED("space-optimized", SpaceOptimizedStrategy::new);

        private final String id;
        private final Supplier<LcsStrategy> factory;

        Strategy(String id, Supplier<LcsStrategy> factory) {
            this.id = id;
            this.factory = factory;
        }

        /** The stable id used by the factory and the system property. */
        String id() {
            return id;
        }

        /** Creates a fresh strategy instance (strategies are stateless). */
        LcsStrategy create() {
            return factory.get();
        }

        /** Looks up a strategy by its id, throwing if none matches. */
        static Strategy fromId(String id) {
            Objects.requireNonNull(id, "strategy id must not be null");
            for (Strategy strategy : values()) {
                if (strategy.id.equals(id)) {
                    return strategy;
                }
            }
            throw new IllegalArgumentException(
                "unknown LCS strategy '" + id + "'; valid ids: " + validIds());
        }

        private static String validIds() {
            StringBuilder ids = new StringBuilder();
            for (Strategy strategy : values()) {
                if (ids.length() > 0) {
                    ids.append(", ");
                }
                ids.append(strategy.id);
            }
            return ids.toString();
        }
    }

    /**
     * Factory method: returns a new strategy for the given id (see
     * {@link Strategy#id()}).
     *
     * @throws IllegalArgumentException if {@code id} matches no strategy
     * @throws NullPointerException     if {@code id} is null
     */
    static LcsStrategy create(String id) {
        return Strategy.fromId(id).create();
    }

    /** Strategy backing the convenience methods; configurable at runtime. */
    private static volatile LcsStrategy defaultStrategy = resolveConfiguredStrategy();

    /** Returns the strategy currently backing the convenience methods. */
    static LcsStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    /**
     * Overrides the default strategy programmatically — an alternative to the
     * {@value #STRATEGY_PROPERTY} system property.
     *
     * @throws NullPointerException if {@code strategy} is null
     */
    static void setDefaultStrategy(LcsStrategy strategy) {
        defaultStrategy = Objects.requireNonNull(strategy, "strategy must not be null");
    }

    private static LcsStrategy resolveConfiguredStrategy() {
        String id = System.getProperty(STRATEGY_PROPERTY);
        if (id == null) {
            return Strategy.FULL_TABLE.create();
        }
        try {
            return create(id);
        } catch (IllegalArgumentException e) {
            System.err.println("[LongestCommonSubsequence] " + e.getMessage()
                + "; falling back to " + Strategy.FULL_TABLE.id());
            return Strategy.FULL_TABLE.create();
        }
    }

    /** Convenience: LCS length using the configured default strategy. */
    static int length(String first, String second) {
        return defaultStrategy.length(first, second);
    }

    /** Convenience: one LCS string using the configured default strategy. */
    static String subsequence(String first, String second) {
        return defaultStrategy.subsequence(first, second);
    }

    public static void main(String[] args) {
        String first = "AGGTAB";
        String second = "GXTXAYB";

        System.out.println("Default strategy: "
            + getDefaultStrategy().getClass().getSimpleName()
            + "  (set -D" + STRATEGY_PROPERTY + "=<id> to change)");
        System.out.println("Using default:    length=" + length(first, second)
            + "  lcs=" + subsequence(first, second));
        System.out.println();

        for (Strategy strategy : Strategy.values()) {
            LcsStrategy impl = create(strategy.id());
            System.out.printf(
                "%-16s -> %-22s length=%d  lcs=%s%n",
                strategy.id(),
                impl.getClass().getSimpleName(),
                impl.length(first, second),
                impl.subsequence(first, second));
        }
    }
}
