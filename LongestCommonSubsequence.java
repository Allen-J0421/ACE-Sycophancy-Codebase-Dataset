import java.util.Objects;

/**
 * Computes the length of the longest common subsequence between two strings.
 */
public final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {
        // Utility class.
    }

    public static int lcs(CharSequence first, CharSequence second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");

        if (first.isEmpty() || second.isEmpty()) {
            return 0;
        }

        if (second.length() > first.length()) {
            return lcs(second, first);
        }

        return lcsLength(first, second);
    }

    private static int lcsLength(CharSequence longer, CharSequence shorter) {
        int[] lengths = new int[shorter.length() + 1];

        for (int i = 1; i <= longer.length(); i++) {
            char longerChar = longer.charAt(i - 1);
            int previousDiagonal = 0;

            for (int j = 1; j <= shorter.length(); j++) {
                int saved = lengths[j];
                if (longerChar == shorter.charAt(j - 1)) {
                    lengths[j] = previousDiagonal + 1;
                } else {
                    lengths[j] = Math.max(lengths[j], lengths[j - 1]);
                }
                previousDiagonal = saved;
            }
        }

        return lengths[shorter.length()];
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            runSample();
            return;
        }

        if (args.length == 2) {
            System.out.println(lcs(args[0], args[1]));
            return;
        }

        System.err.println("Usage: java LongestCommonSubsequence <first> <second>");
        System.exit(1);
    }

    static void runSample() {
        String first = "AGGTAB";
        String second = "GXTXAYB";
        System.out.println(lcs(first, second));
    }
}
