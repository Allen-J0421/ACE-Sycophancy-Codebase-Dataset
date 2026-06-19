final class LongestCommonSubsequence {
    private static final SequencePair SAMPLE_INPUT = new SequencePair("AGGTAB", "GXTXAYB");

    private LongestCommonSubsequence() {
    }

    static int lcs(String first, String second) {
        return longestCommonSubsequenceLength(first, second);
    }

    static int longestCommonSubsequenceLength(String first, String second) {
        SequencePair orderedInput = orderedByLength(first, second);
        char[] rows = orderedInput.first.toCharArray();
        char[] columns = orderedInput.second.toCharArray();
        int columnCount = columns.length;

        int[] subsequenceLengths = new int[columnCount + 1];

        for (char rowCharacter : rows) {
            int previousDiagonal = 0;

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                int currentColumn = columnIndex + 1;
                int previousColumnLength = subsequenceLengths[currentColumn];

                if (rowCharacter == columns[columnIndex]) {
                    subsequenceLengths[currentColumn] = previousDiagonal + 1;
                } else {
                    subsequenceLengths[currentColumn] = Math.max(
                            subsequenceLengths[currentColumn],
                            subsequenceLengths[columnIndex]);
                }

                previousDiagonal = previousColumnLength;
            }
        }

        return subsequenceLengths[columnCount];
    }

    private static SequencePair orderedByLength(String first, String second) {
        return first.length() >= second.length()
                ? new SequencePair(first, second)
                : new SequencePair(second, first);
    }

    public static void main(String[] args) {
        SequencePair input = inputPair(args);
        System.out.println(longestCommonSubsequenceLength(input.first, input.second));
    }

    private static SequencePair inputPair(String[] args) {
        return hasInputPair(args) ? new SequencePair(args[0], args[1]) : SAMPLE_INPUT;
    }

    private static boolean hasInputPair(String[] args) {
        return args.length >= 2;
    }

    private static final class SequencePair {
        private final String first;
        private final String second;

        private SequencePair(String first, String second) {
            this.first = first;
            this.second = second;
        }
    }
}
