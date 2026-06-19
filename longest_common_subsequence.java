class LongestCommonSubsequence {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";

    static int lcs(String first, String second) {
        return longestCommonSubsequenceLength(first, second);
    }

    static int longestCommonSubsequenceLength(String first, String second) {
        char[] rows = longerOf(first, second).toCharArray();
        char[] columns = shorterOf(first, second).toCharArray();
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

    private static String longerOf(String first, String second) {
        return first.length() >= second.length() ? first : second;
    }

    private static String shorterOf(String first, String second) {
        return first.length() < second.length() ? first : second;
    }

    public static void main(String[] args) {
        String first = firstInput(args);
        String second = secondInput(args);
        System.out.println(longestCommonSubsequenceLength(first, second));
    }

    private static String firstInput(String[] args) {
        return hasInputPair(args) ? args[0] : SAMPLE_FIRST;
    }

    private static String secondInput(String[] args) {
        return hasInputPair(args) ? args[1] : SAMPLE_SECOND;
    }

    private static boolean hasInputPair(String[] args) {
        return args.length >= 2;
    }
}
