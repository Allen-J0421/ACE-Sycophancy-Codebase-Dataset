class LongestCommonSubsequence {
    private static final String SAMPLE_FIRST = "AGGTAB";
    private static final String SAMPLE_SECOND = "GXTXAYB";

    static int lcs(String first, String second) {
        return longestCommonSubsequenceLength(first, second);
    }

    static int longestCommonSubsequenceLength(String first, String second) {
        String rows = longerOf(first, second);
        String columns = shorterOf(first, second);
        int columnCount = columns.length();

        int[] subsequenceLengths = new int[columnCount + 1];

        for (int rowIndex = 0; rowIndex < rows.length(); rowIndex++) {
            char rowCharacter = rows.charAt(rowIndex);
            int previousDiagonal = 0;

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                int currentColumn = columnIndex + 1;
                int previousColumnLength = subsequenceLengths[currentColumn];

                if (rowCharacter == columns.charAt(columnIndex)) {
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
        String first = inputOrDefault(args, 0, SAMPLE_FIRST);
        String second = inputOrDefault(args, 1, SAMPLE_SECOND);
        System.out.println(longestCommonSubsequenceLength(first, second));
    }

    private static String inputOrDefault(String[] args, int index, String defaultValue) {
        return args.length > index ? args[index] : defaultValue;
    }
}
