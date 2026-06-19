import java.util.Objects;

final class LongestCommonSubsequence {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";

    private LongestCommonSubsequence() {
    }

    static int lcs(String first, String second) {
        SequencePair sequences = SequencePair.from(first, second);

        if (sequences.hasEmptySequence()) {
            return 0;
        }

        if (sequences.areIdentical()) {
            return sequences.longerLength();
        }

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

    private static final class SequencePair {
        private final String first;
        private final String second;
        private final char[] longer;
        private final char[] shorter;

        private SequencePair(String first, String second, char[] longer, char[] shorter) {
            this.first = first;
            this.second = second;
            this.longer = longer;
            this.shorter = shorter;
        }

        static SequencePair from(String first, String second) {
            Objects.requireNonNull(first, "first must not be null");
            Objects.requireNonNull(second, "second must not be null");

            char[] firstChars = first.toCharArray();
            char[] secondChars = second.toCharArray();

            if (firstChars.length >= secondChars.length) {
                return new SequencePair(first, second, firstChars, secondChars);
            }

            return new SequencePair(first, second, secondChars, firstChars);
        }

        boolean hasEmptySequence() {
            return shorter.length == 0;
        }

        boolean areIdentical() {
            return first.equals(second);
        }

        int longerLength() {
            return longer.length;
        }

        char[] longer() {
            return longer;
        }

        char[] shorter() {
            return shorter;
        }
    }
}
