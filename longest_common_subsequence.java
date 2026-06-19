class LongestCommonSubsequence {

    static int lcs(String first, String second) {
        int firstLength = first.length();
        int secondLength = second.length();

        int[] previousRow = new int[secondLength + 1];
        int[] currentRow = new int[secondLength + 1];

        for (int firstIndex = 1; firstIndex <= firstLength; firstIndex++) {
            for (int secondIndex = 1; secondIndex <= secondLength; secondIndex++) {
                if (first.charAt(firstIndex - 1) == second.charAt(secondIndex - 1)) {
                    currentRow[secondIndex] = previousRow[secondIndex - 1] + 1;
                } else {
                    currentRow[secondIndex] = Math.max(
                            previousRow[secondIndex],
                            currentRow[secondIndex - 1]);
                }
            }

            int[] completedRow = previousRow;
            previousRow = currentRow;
            currentRow = completedRow;
        }

        return previousRow[secondLength];
    }

    public static void main(String[] args) {
        String first = "AGGTAB";
        String second = "GXTXAYB";
        System.out.println(lcs(first, second));
    }
}
