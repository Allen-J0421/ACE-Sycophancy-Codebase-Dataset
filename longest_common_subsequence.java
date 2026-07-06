@FunctionalInterface
interface LcsStrategy {
    int compute(String s1, String s2);
}

enum LcsStrategyType {
    FULL_TABLE,
    SPACE_OPTIMIZED
}

class LcsResult {
    private final int length;
    private final LcsStrategyType strategyType;
    private final long elapsedNanos;

    LcsResult(int length, LcsStrategyType strategyType, long elapsedNanos) {
        this.length        = length;
        this.strategyType  = strategyType;
        this.elapsedNanos  = elapsedNanos;
    }

    int getLength()                  { return length; }
    LcsStrategyType getStrategyType() { return strategyType; }
    long getElapsedNanos()           { return elapsedNanos; }

    @Override
    public String toString() {
        return "LcsResult{length=" + length
                + ", strategy=" + strategyType
                + ", elapsedNanos=" + elapsedNanos + "}";
    }
}

class FullTableLcsStrategy implements LcsStrategy {
    @Override
    public int compute(String s1, String s2) {
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
}

class SpaceOptimizedLcsStrategy implements LcsStrategy {
    @Override
    public int compute(String s1, String s2) {
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

class LcsStrategyFactory {
    static LcsStrategy create(LcsStrategyType type) {
        switch (type) {
            case SPACE_OPTIMIZED: return new SpaceOptimizedLcsStrategy();
            default:              return new FullTableLcsStrategy();
        }
    }
}

class LcsService {
    private final LcsStrategy strategy;
    private final LcsStrategyType strategyType;
    private final boolean validateInputs;
    private final boolean caseInsensitive;

    private LcsService(Builder builder) {
        this.strategy        = builder.strategy;
        this.strategyType    = builder.strategyType;
        this.validateInputs  = builder.validateInputs;
        this.caseInsensitive = builder.caseInsensitive;
    }

    LcsResult compute(String s1, String s2) {
        if (validateInputs) {
            if (s1 == null || s2 == null)
                throw new IllegalArgumentException("Inputs must not be null");
        }
        if (caseInsensitive) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
        }
        long start = System.nanoTime();
        int length = strategy.compute(s1, s2);
        long elapsed = System.nanoTime() - start;
        return new LcsResult(length, strategyType, elapsed);
    }

    static class Builder {
        private LcsStrategyType strategyType = LcsStrategyType.FULL_TABLE;
        private LcsStrategy strategy         = LcsStrategyFactory.create(strategyType);
        private boolean validateInputs       = false;
        private boolean caseInsensitive      = false;

        Builder strategy(LcsStrategyType type) {
            this.strategyType = type;
            this.strategy     = LcsStrategyFactory.create(type);
            return this;
        }

        Builder validateInputs(boolean validate) {
            this.validateInputs = validate;
            return this;
        }

        Builder caseInsensitive(boolean caseInsensitive) {
            this.caseInsensitive = caseInsensitive;
            return this;
        }

        LcsService build() {
            return new LcsService(this);
        }
    }
}

class LongestCommonSubsequence {

    static LcsResult lcs(String s1, String s2) {
        return new LcsService.Builder()
                .strategy(LcsStrategyType.FULL_TABLE)
                .validateInputs(true)
                .build()
                .compute(s1, s2);
    }

    public static void main(String[] args) {
        String S1 = "AGGTAB";
        String S2 = "GXTXAYB";
        LcsResult result = lcs(S1, S2);
        System.out.println(result.getLength());
        System.out.println(result);
    }
}
