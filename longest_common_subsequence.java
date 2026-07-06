@FunctionalInterface
interface LcsStrategy {
    int compute(String s1, String s2);
}

@FunctionalInterface
interface LcsPipelineStep {
    String[] apply(String s1, String s2);
}

enum LcsStrategyType {
    FULL_TABLE,
    SPACE_OPTIMIZED
}

class LcsException extends RuntimeException {
    LcsException(String message) { super(message); }
    LcsException(String message, Throwable cause) { super(message, cause); }
}

class LcsValidationException extends LcsException {
    private final String paramName;

    LcsValidationException(String paramName, String message) {
        super(message);
        this.paramName = paramName;
    }

    String getParamName() { return paramName; }
}

class LcsResult {
    private final int length;
    private final LcsStrategyType strategyType;
    private final long elapsedNanos;

    LcsResult(int length, LcsStrategyType strategyType, long elapsedNanos) {
        this.length       = length;
        this.strategyType = strategyType;
        this.elapsedNanos = elapsedNanos;
    }

    int getLength()                   { return length; }
    LcsStrategyType getStrategyType() { return strategyType; }
    long getElapsedNanos()            { return elapsedNanos; }

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

class InstrumentedLcsStrategy implements LcsStrategy {
    private final LcsStrategy delegate;
    private long lastElapsedNanos;

    InstrumentedLcsStrategy(LcsStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compute(String s1, String s2) {
        long start = System.nanoTime();
        int result = delegate.compute(s1, s2);
        lastElapsedNanos = System.nanoTime() - start;
        return result;
    }

    long getLastElapsedNanos() { return lastElapsedNanos; }
}

class LcsStrategyFactory {
    static LcsStrategy create(LcsStrategyType type) {
        switch (type) {
            case SPACE_OPTIMIZED: return new SpaceOptimizedLcsStrategy();
            default:              return new FullTableLcsStrategy();
        }
    }
}

class LcsInputValidator {
    static void validate(String s1, String s2) {
        if (s1 == null)
            throw new LcsValidationException("s1", "First input string must not be null");
        if (s2 == null)
            throw new LcsValidationException("s2", "Second input string must not be null");
    }
}

class LcsPreprocessor {
    private final java.util.List<LcsPipelineStep> steps;

    private LcsPreprocessor(java.util.List<LcsPipelineStep> steps) {
        this.steps = java.util.Collections.unmodifiableList(steps);
    }

    static LcsPreprocessor of(java.util.List<LcsPipelineStep> steps) {
        return new LcsPreprocessor(new java.util.ArrayList<>(steps));
    }

    String[] process(String s1, String s2) {
        String[] pair = {s1, s2};
        for (LcsPipelineStep step : steps) {
            pair = step.apply(pair[0], pair[1]);
        }
        return pair;
    }

    static LcsPipelineStep validationStep() {
        return (s1, s2) -> {
            LcsInputValidator.validate(s1, s2);
            return new String[]{s1, s2};
        };
    }

    static LcsPipelineStep caseNormalizationStep() {
        return (s1, s2) -> new String[]{s1.toLowerCase(), s2.toLowerCase()};
    }
}

class LcsServiceBuilder {
    private LcsStrategyType strategyType = LcsStrategyType.FULL_TABLE;
    private boolean validateInputs       = false;
    private boolean caseInsensitive      = false;

    LcsServiceBuilder strategy(LcsStrategyType type) {
        this.strategyType = type;
        return this;
    }

    LcsServiceBuilder validateInputs(boolean validate) {
        this.validateInputs = validate;
        return this;
    }

    LcsServiceBuilder caseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
        return this;
    }

    LcsService build() {
        java.util.List<LcsPipelineStep> steps = new java.util.ArrayList<>();
        if (validateInputs)  steps.add(LcsPreprocessor.validationStep());
        if (caseInsensitive) steps.add(LcsPreprocessor.caseNormalizationStep());
        return new LcsService(
            new InstrumentedLcsStrategy(LcsStrategyFactory.create(strategyType)),
            strategyType,
            LcsPreprocessor.of(steps)
        );
    }

    LcsResult compute(String s1, String s2) {
        return build().compute(s1, s2);
    }
}

class LcsService {
    private final InstrumentedLcsStrategy strategy;
    private final LcsStrategyType strategyType;
    private final LcsPreprocessor preprocessor;

    LcsService(InstrumentedLcsStrategy strategy, LcsStrategyType strategyType,
               LcsPreprocessor preprocessor) {
        this.strategy     = strategy;
        this.strategyType = strategyType;
        this.preprocessor = preprocessor;
    }

    static LcsServiceBuilder configure() {
        return new LcsServiceBuilder();
    }

    LcsResult compute(String s1, String s2) {
        String[] inputs = preprocessor.process(s1, s2);
        int length = strategy.compute(inputs[0], inputs[1]);
        return new LcsResult(length, strategyType, strategy.getLastElapsedNanos());
    }
}

class LongestCommonSubsequence {

    static LcsResult lcs(String s1, String s2) {
        return LcsService.configure()
                .strategy(LcsStrategyType.FULL_TABLE)
                .validateInputs(true)
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
