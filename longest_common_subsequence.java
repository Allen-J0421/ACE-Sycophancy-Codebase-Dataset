class LcsService {
    private final int[][] dp;
    private final int m;
    private final int n;

    LcsService(String s1, String s2) {
        this.m = s1.length();
        this.n = s2.length();
        this.dp = new int[m + 1][n + 1];
        buildTable(s1, s2);
    }

    private void buildTable(String s1, String s2) {
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
    }

    int getLength() {
        return dp[m][n];
    }
}

class LongestCommonSubsequence {

    static int lcs(String s1, String s2) {
        return new LcsService(s1, s2).getLength();
    }

    public static void main(String[] args) {
        String S1 = "AGGTAB";
        String S2 = "GXTXAYB";
        System.out.println(lcs(S1, S2));
    }
}
