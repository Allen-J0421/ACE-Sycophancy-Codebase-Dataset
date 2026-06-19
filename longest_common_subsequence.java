import java.util.Objects;

final class LongestCommonSubsequence {

    private LongestCommonSubsequence() {
    }

    static int lcs(String first, String second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        int[][] lengths = buildLengthTable(first.toCharArray(), second.toCharArray());
        return lengths[first.length()][second.length()];
    }

    private static int[][] buildLengthTable(char[] first, char[] second) {
        int[][] lengths = new int[first.length + 1][second.length + 1];

        for (int i = 1; i <= first.length; i++) {
            for (int j = 1; j <= second.length; j++) {
                if (first[i - 1] == second[j - 1]) {
                    lengths[i][j] = lengths[i - 1][j - 1] + 1;
                    continue;
                }

                lengths[i][j] = Math.max(lengths[i - 1][j], lengths[i][j - 1]);
            }
        }

        return lengths;
    }

    public static void main(String[] args) {
        String first = "AGGTAB";
        String second = "GXTXAYB";

        System.out.println(lcs(first, second));
    }
}
