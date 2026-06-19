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
        return LongestCommonSubsequenceSolver.length(first, second);
    }

    public static LcsResult analyze(CharSequence first, CharSequence second) {
        return LongestCommonSubsequenceSolver.analyze(first, second);
    }
}
