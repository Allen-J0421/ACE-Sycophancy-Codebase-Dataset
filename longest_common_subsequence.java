import java.util.Objects;

final class LongestCommonSubsequence {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";

    private LongestCommonSubsequence() {
    }

    static int lcs(String first, String second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        char[] longer = first.toCharArray();
        char[] shorter = second.toCharArray();

        if (shorter.length > longer.length) {
            char[] swap = longer;
            longer = shorter;
            shorter = swap;
        }

        return computeLcsLength(longer, shorter);
    }

    private static int computeLcsLength(char[] longer, char[] shorter) {
        int[] lengths = new int[shorter.length + 1];

        for (int i = 1; i <= longer.length; i++) {
            int diagonal = 0;

            for (int j = 1; j <= shorter.length; j++) {
                int previousRow = lengths[j];

                if (longer[i - 1] == shorter[j - 1]) {
                    lengths[j] = diagonal + 1;
                    diagonal = previousRow;
                    continue;
                }

                lengths[j] = Math.max(lengths[j], lengths[j - 1]);
                diagonal = previousRow;
            }
        }

        return lengths[shorter.length];
    }

    public static void main(String[] args) {
        System.out.println(lcs(SAMPLE_FIRST, SAMPLE_SECOND));
    }
}
