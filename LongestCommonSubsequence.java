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

        return new Solver(first, second).solve();
    }
    private static final class Solver {
        private final char[] longer;
        private final char[] shorter;
        private final int[] lengths;

        private Solver(String first, String second) {
            char[] firstChars = first.toCharArray();
            char[] secondChars = second.toCharArray();

            if (firstChars.length >= secondChars.length) {
                this.longer = firstChars;
                this.shorter = secondChars;
            } else {
                this.longer = secondChars;
                this.shorter = firstChars;
            }

            this.lengths = new int[this.shorter.length + 1];
        }

        private int solve() {
            for (char current : longer) {
                updateLengths(current);
            }

            return lengths[shorter.length];
        }

        private void updateLengths(char current) {
            int diagonal = 0;

            for (int j = 1; j <= shorter.length; j++) {
                int previousRow = lengths[j];

                lengths[j] = current == shorter[j - 1]
                    ? diagonal + 1
                    : Math.max(lengths[j], lengths[j - 1]);
                diagonal = previousRow;
            }
        }
    }
}
