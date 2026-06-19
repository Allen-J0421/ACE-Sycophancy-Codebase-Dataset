class LongestCommonSubsequence {

    static int lcs(String first, String second) {
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
        String first = "AGGTAB";
        String second = "GXTXAYB";
        System.out.println(lcs(first, second));
    }
}
