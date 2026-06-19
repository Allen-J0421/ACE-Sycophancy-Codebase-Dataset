package lcs;

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

        CharSequence longer = first;
        CharSequence shorter = second;
        if (shorter.length() > longer.length()) {
            longer = second;
            shorter = first;
        }

        return lcsLength(longer, shorter);
    }

    public static LcsResult analyze(CharSequence first, CharSequence second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");

        if (first.isEmpty() || second.isEmpty()) {
            return new LcsResult(0, "");
        }

        return analyzeWithTable(first, second);
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

    private static LcsResult analyzeWithTable(CharSequence longer, CharSequence shorter) {
        int[][] lengths = new int[longer.length() + 1][shorter.length() + 1];

        for (int i = 1; i <= longer.length(); i++) {
            char longerChar = longer.charAt(i - 1);
            for (int j = 1; j <= shorter.length(); j++) {
                if (longerChar == shorter.charAt(j - 1)) {
                    lengths[i][j] = lengths[i - 1][j - 1] + 1;
                } else {
                    lengths[i][j] = Math.max(lengths[i - 1][j], lengths[i][j - 1]);
                }
            }
        }

        StringBuilder subsequence = new StringBuilder(lengths[longer.length()][shorter.length()]);
        int i = longer.length();
        int j = shorter.length();
        while (i > 0 && j > 0) {
            if (longer.charAt(i - 1) == shorter.charAt(j - 1)) {
                subsequence.append(longer.charAt(i - 1));
                i--;
                j--;
            } else if (lengths[i - 1][j] >= lengths[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return new LcsResult(lengths[longer.length()][shorter.length()], subsequence.reverse().toString());
    }
}
