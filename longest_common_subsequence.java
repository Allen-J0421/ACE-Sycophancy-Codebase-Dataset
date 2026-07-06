@FunctionalInterface
interface LcsStrategy {
    int compute(String s1, String s2);
}

enum LcsStrategyType {
    FULL_TABLE,
    SPACE_OPTIMIZED
}

class LcsStrategyFactory {
    static LcsStrategy create(LcsStrategyType type) {
        switch (type) {
            case SPACE_OPTIMIZED: return LcsService::spaceOptimized;
            default:              return LcsService::fullTable;
        }
    }
}

class LcsService {
    private final LcsStrategy strategy;

    LcsService(LcsStrategy strategy) {
        this.strategy = strategy;
    }

    int getLength(String s1, String s2) {
        return strategy.compute(s1, s2);
    }

    static int fullTable(String s1, String s2) {
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
        return dp[m][n];
    }

    static int spaceOptimized(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
            java.util.Arrays.fill(curr, 0);
        }
        return prev[n];
    }
}

class LongestCommonSubsequence {
    private static final LcsStrategyType DEFAULT_STRATEGY = LcsStrategyType.FULL_TABLE;

    static int lcs(String s1, String s2) {
        return new LcsService(LcsStrategyFactory.create(DEFAULT_STRATEGY)).getLength(s1, s2);
    }

    public static void main(String[] args) {
        String S1 = "AGGTAB";
        String S2 = "GXTXAYB";
        System.out.println(lcs(S1, S2));
    }
}
