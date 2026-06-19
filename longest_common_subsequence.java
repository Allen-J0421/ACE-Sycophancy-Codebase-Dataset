final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {
        // Utility class.
    }

    static int lcs(String first, String second) {
        int firstLength = first.length();
        int secondLength = second.length();

        int[][] lengths = new int[firstLength + 1][secondLength + 1];

        for (int i = 1; i <= firstLength; i++) {
            char firstChar = first.charAt(i - 1);
            for (int j = 1; j <= secondLength; j++) {
                if (firstChar == second.charAt(j - 1)) {
                    lengths[i][j] = lengths[i - 1][j - 1] + 1;
                } else {
                    lengths[i][j] = Math.max(lengths[i - 1][j], lengths[i][j - 1]);
                }
            }
        }

        return lengths[firstLength][secondLength];
    }

    public static void main(String[] args) {
        String first = "AGGTAB";
        String second = "GXTXAYB";
        System.out.println(lcs(first, second));
    }
}
