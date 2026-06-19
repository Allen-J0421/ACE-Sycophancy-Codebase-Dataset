class LongestCommonSubsequence {

    static int lcs(String first, String second) {
        String rows = first.length() >= second.length() ? first : second;
        String columns = first.length() < second.length() ? first : second;
        int columnCount = columns.length();

        int[] previousRow = new int[columnCount + 1];
        int[] currentRow = new int[columnCount + 1];

        for (int rowIndex = 0; rowIndex < rows.length(); rowIndex++) {
            char rowCharacter = rows.charAt(rowIndex);

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                int currentColumn = columnIndex + 1;

                if (rowCharacter == columns.charAt(columnIndex)) {
                    currentRow[currentColumn] = previousRow[columnIndex] + 1;
                } else {
                    currentRow[currentColumn] = Math.max(
                            previousRow[currentColumn],
                            currentRow[columnIndex]);
                }
            }

            int[] completedRow = previousRow;
            previousRow = currentRow;
            currentRow = completedRow;
        }

        return previousRow[columnCount];
    }

    public static void main(String[] args) {
        String first = "AGGTAB";
        String second = "GXTXAYB";
        System.out.println(lcs(first, second));
    }
}
