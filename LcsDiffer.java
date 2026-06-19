/**
 * String diffing utilities for visualizing LCS results.
 * Creates human-readable diff representations showing which characters matched.
 */
class LcsDiffer {

    /**
     * Creates a visual diff showing matched and unmatched characters.
     * Marks characters in the LCS with markers, unmatched with spaces.
     *
     * @param s1        first string
     * @param s2        second string
     * @param lcs       the LCS string
     * @return formatted diff
     */
    static String visualDiff(String s1, String s2, String lcs) {
        StringBuilder result = new StringBuilder();
        result.append("Visual Diff\n");
        result.append("===========\n\n");

        // Alignment visualization
        result.append("String 1: ").append(s1).append("\n");
        result.append("String 2: ").append(s2).append("\n");
        result.append("LCS:      ").append(lcs).append("\n\n");

        // Character-by-character breakdown
        result.append("Matched Characters (in LCS):\n");
        for (char c : lcs.toCharArray()) {
            result.append("  '").append(c).append("'\n");
        }

        return result.toString();
    }

    /**
     * Creates a simple ASCII-art diff showing differences.
     * Uses + for characters in first string only, - for second string only, = for matches.
     *
     * @param s1  first string
     * @param s2  second string
     * @param lcs the LCS string
     * @return ASCII diff
     */
    static String asciiDiff(String s1, String s2, String lcs) {
        StringBuilder result = new StringBuilder();

        // Mark which characters are in LCS
        boolean[] inLcs1 = new boolean[s1.length()];
        boolean[] inLcs2 = new boolean[s2.length()];

        markLcsCharacters(s1, s2, lcs, inLcs1, inLcs2);

        result.append("String 1: ");
        for (int i = 0; i < s1.length(); i++) {
            result.append(inLcs1[i] ? "=" : "+");
        }
        result.append("\n");
        result.append("          ").append(s1).append("\n\n");

        result.append("String 2: ");
        for (int i = 0; i < s2.length(); i++) {
            result.append(inLcs2[i] ? "=" : "-");
        }
        result.append("\n");
        result.append("          ").append(s2).append("\n");

        return result.toString();
    }

    /**
     * Creates a side-by-side diff for easy comparison.
     *
     * @param s1  first string
     * @param s2  second string
     * @param lcs the LCS string
     * @return side-by-side diff
     */
    static String sideBySideDiff(String s1, String s2, String lcs) {
        StringBuilder result = new StringBuilder();
        int maxLen = Math.max(s1.length(), s2.length());

        result.append(String.format("%-30s | %-30s\n", "String 1", "String 2"));
        result.append("-".repeat(63)).append("\n");

        for (int i = 0; i < maxLen; i++) {
            String c1 = i < s1.length() ? String.valueOf(s1.charAt(i)) : " ";
            String c2 = i < s2.length() ? String.valueOf(s2.charAt(i)) : " ";
            result.append(String.format("%-30s | %-30s\n", c1, c2));
        }

        result.append("\nLCS: ").append(lcs).append(" (length: ").append(lcs.length()).append(")");

        return result.toString();
    }

    /**
     * Creates a markdown-formatted diff (useful for docs/reports).
     *
     * @param s1  first string
     * @param s2  second string
     * @param lcs the LCS string
     * @return markdown diff
     */
    static String markdownDiff(String s1, String s2, String lcs) {
        StringBuilder result = new StringBuilder();

        result.append("## String Comparison\n\n");
        result.append("**String 1**: `").append(escapeMarkdown(s1)).append("`\n\n");
        result.append("**String 2**: `").append(escapeMarkdown(s2)).append("`\n\n");
        result.append("**LCS**: `").append(escapeMarkdown(lcs)).append("`\n\n");

        result.append("### Metrics\n\n");
        int length = lcs.length();
        double similarity = LcsAnalyzer.similarityRatio(length, s1.length(), s2.length());

        result.append("- **LCS Length**: ").append(length).append("\n");
        result.append("- **String 1 Length**: ").append(s1.length()).append("\n");
        result.append("- **String 2 Length**: ").append(s2.length()).append("\n");
        result.append("- **Similarity**: ").append(String.format("%.1f%%", similarity)).append("\n");

        return result.toString();
    }

    /**
     * Marks which characters in s1 and s2 are part of the LCS.
     * Simple greedy approach: marks first occurrence of each LCS character.
     *
     * @param s1      first string
     * @param s2      second string
     * @param lcs     LCS string
     * @param inLcs1  boolean array marking positions in s1
     * @param inLcs2  boolean array marking positions in s2
     */
    private static void markLcsCharacters(String s1, String s2, String lcs,
                                         boolean[] inLcs1, boolean[] inLcs2) {
        int pos1 = 0, pos2 = 0;

        for (char c : lcs.toCharArray()) {
            // Find next occurrence of c in s1
            while (pos1 < s1.length() && s1.charAt(pos1) != c) {
                pos1++;
            }
            if (pos1 < s1.length()) {
                inLcs1[pos1] = true;
                pos1++;
            }

            // Find next occurrence of c in s2
            while (pos2 < s2.length() && s2.charAt(pos2) != c) {
                pos2++;
            }
            if (pos2 < s2.length()) {
                inLcs2[pos2] = true;
                pos2++;
            }
        }
    }

    /**
     * Escapes special markdown characters.
     */
    private static String escapeMarkdown(String s) {
        return s.replace("`", "\\`").replace("*", "\\*").replace("_", "\\_");
    }
}
