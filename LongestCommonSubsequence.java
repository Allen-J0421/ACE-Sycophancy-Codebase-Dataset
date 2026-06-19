import java.util.Objects;

public final class LongestCommonSubsequence {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";

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

        return computeLcsLength(normalizeByLength(first, second));
    }

    private static int computeLcsLength(NormalizedSequences sequences) {
        char[] longer = sequences.longer();
        char[] shorter = sequences.shorter();
        int[] lengths = new int[shorter.length + 1];

        for (char current : longer) {
            int diagonal = 0;

            for (int j = 1; j <= shorter.length; j++) {
                int previousRow = lengths[j];

                lengths[j] = current == shorter[j - 1]
                    ? diagonal + 1
                    : Math.max(lengths[j], lengths[j - 1]);
                diagonal = previousRow;
            }
        }

        return lengths[shorter.length];
    }

    private static NormalizedSequences normalizeByLength(String first, String second) {
        char[] firstChars = first.toCharArray();
        char[] secondChars = second.toCharArray();

        if (firstChars.length >= secondChars.length) {
            return new NormalizedSequences(firstChars, secondChars);
        }

        return new NormalizedSequences(secondChars, firstChars);
    }

    public static void main(String[] args) {
        System.out.println(lcs(SAMPLE_FIRST, SAMPLE_SECOND));
    }

    private record NormalizedSequences(char[] longer, char[] shorter) {
    }
}
