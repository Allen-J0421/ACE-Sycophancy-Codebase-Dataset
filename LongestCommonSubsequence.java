public class LongestCommonSubsequence {

    public LcsResult compute(LcsInput input) {
        String s1 = input.getFirst();
        String s2 = input.getSecond();
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return new LcsResult(dp[m][n], backtrack(dp, s1, s2, m, n));
    }

    private String backtrack(int[][] dp, String s1, String s2, int i, int j) {
        StringBuilder sb = new StringBuilder();
        while (i > 0 && j > 0) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                sb.insert(0, s1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return sb.toString();
    }
}
