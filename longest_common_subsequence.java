import java.util.Objects;

final class LongestCommonSubsequence {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";

    private LongestCommonSubsequence() {
    }

    static int lcs(String first, String second) {
        requireSequence(first, "first");
        requireSequence(second, "second");

        if (first.isEmpty() || second.isEmpty()) {
            return 0;
        }

        if (first.equals(second)) {
            return first.length();
        }

        CharArrayPair sequences = normalizeByLength(first, second);
        return computeLcsLength(sequences.longer(), sequences.shorter());
    }

    private static int computeLcsLength(char[] longer, char[] shorter) {
        int[] lengths = new int[shorter.length + 1];

        for (int i = 1; i <= longer.length; i++) {
            int diagonal = 0;
            char current = longer[i - 1];

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

    public static void main(String[] args) {
        System.out.println(lcs(SAMPLE_FIRST, SAMPLE_SECOND));
    }

    private static void requireSequence(String sequence, String name) {
        Objects.requireNonNull(sequence, name + " must not be null");
    }

    private static CharArrayPair normalizeByLength(String first, String second) {
        char[] firstChars = first.toCharArray();
        char[] secondChars = second.toCharArray();

        if (firstChars.length >= secondChars.length) {
            return new CharArrayPair(firstChars, secondChars);
        }

        return new CharArrayPair(secondChars, firstChars);
    }

    private static final class CharArrayPair {
        private final char[] longer;
        private final char[] shorter;

        private CharArrayPair(char[] longer, char[] shorter) {
            this.longer = longer;
            this.shorter = shorter;
        }

        char[] longer() {
            return longer;
        }

        char[] shorter() {
            return shorter;
        }
    }
}
