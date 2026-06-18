import java.util.Arrays;

class LongestCommonSubsequence {

    static int lcs(String S1, String S2) {
        int m = S1.length();
        int n = S2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (S1.charAt(i - 1) == S2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                }
                else {
                    dp[i][j] = Math.max(dp[i - 1][j],
                                        dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args)
    {
        String S1 = "AGGTAB";
        String S2 = "GXTXAYB";
        System.out.println( lcs(S1, S2));
    }
}
