/**
 * Configuration system for LCS algorithm behavior.
 * Centralizes performance tuning, algorithm selection, and output preferences.
 */
class LcsConfig {

    /**
     * Normalizer preset for configuration.
     */
    enum NormalizerPreset {
        NONE("no normalization", () -> InputNormalizers.none()),
        CASE_INSENSITIVE("lowercase", () -> InputNormalizers.caseInsensitive()),
        WHITESPACE_INSENSITIVE("collapse whitespace", () -> InputNormalizers.whitespaceInsensitive()),
        TEXT_ONLY("lowercase, no whitespace/punctuation", () -> InputNormalizers.textOnly()),
        STRICT_COMPARISON("case-insensitive, collapse whitespace", () -> InputNormalizers.strictComparison());

        final String description;
        final NormalizerFactory factory;

        NormalizerPreset(String description, NormalizerFactory factory) {
            this.description = description;
            this.factory = factory;
        }

        InputNormalizer create() {
            return factory.create();
        }

        interface NormalizerFactory {
            InputNormalizer create();
        }
    }

    /**
     * Algorithm selection for automatic solver routing.
     */
    enum AlgorithmPreference {
        STANDARD("Standard O(mn) space and time"),
        SPACE_OPTIMIZED("O(min(m,n)) space, O(mn) time"),
        APPROXIMATE("Band-narrowing approximation for large inputs"),
        SUBSTRING("Optimized for pattern << text"),
        AUTO("Automatic selection based on input size");

        final String description;

        AlgorithmPreference(String description) {
            this.description = description;
        }
    }

    /**
     * Configuration builder with sensible defaults.
     */
    static class Builder {
        AlgorithmPreference algo = AlgorithmPreference.AUTO;
        boolean enableCache = true;
        boolean enableNormalization = false;
        NormalizerPreset normalizer = NormalizerPreset.STRICT_COMPARISON;
        double approximateSimilarityThreshold = 0.80;
        int approximateBandWidthPercent = 20;
        boolean enableInstrumentation = false;
        int cacheSizeLimit = 1000;

        Builder() {}

        /**
         * Sets algorithm preference.
         */
        Builder algorithm(AlgorithmPreference algo) {
            this.algo = algo;
            return this;
        }

        /**
         * Enables/disables result caching.
         */
        Builder cache(boolean enable) {
            this.enableCache = enable;
            return this;
        }

        /**
         * Enables/disables input normalization.
         */
        Builder normalization(boolean enable) {
            this.enableNormalization = enable;
            return this;
        }

        /**
         * Sets normalization preset.
         */
        Builder normalizer(NormalizerPreset preset) {
            this.normalizer = preset;
            return this;
        }

        /**
         * Sets approximation similarity threshold (0.0-1.0).
         */
        Builder approximateThreshold(double threshold) {
            if (threshold < 0.0 || threshold > 1.0) {
                throw new IllegalArgumentException("Threshold must be 0.0-1.0");
            }
            this.approximateSimilarityThreshold = threshold;
            return this;
        }

        /**
         * Sets approximation band width as % of diagonal.
         */
        Builder approximateBandWidth(int percent) {
            if (percent < 1 || percent > 100) {
                throw new IllegalArgumentException("Band width must be 1-100%");
            }
            this.approximateBandWidthPercent = percent;
            return this;
        }

        /**
         * Enables/disables instrumentation.
         */
        Builder instrumentation(boolean enable) {
            this.enableInstrumentation = enable;
            return this;
        }

        /**
         * Sets maximum cache entries.
         */
        Builder cacheSizeLimit(int limit) {
            if (limit <= 0) {
                throw new IllegalArgumentException("Cache size must be positive");
            }
            this.cacheSizeLimit = limit;
            return this;
        }

        /**
         * Builds configured solver based on settings.
         */
        LcsSolver buildSolver() {
            LcsSolver solver = selectSolver();

            // Apply caching if enabled
            if (enableCache) {
                solver = LcsSolverFactory.withCaching(solver);
            }

            // Apply normalization if enabled
            if (enableNormalization) {
                solver = new NormalizingLcsSolver(solver, normalizer.create());
            }

            return solver;
        }

        private LcsSolver selectSolver() {
            switch (algo) {
                case STANDARD:
                    return new StandardLcsSolver();
                case SPACE_OPTIMIZED:
                    return new SpaceOptimizedLcsSolver();
                case APPROXIMATE:
                    return new ApproximateLcsSolver(approximateSimilarityThreshold, approximateBandWidthPercent);
                case SUBSTRING:
                    return new SubstringLcsSolver();
                case AUTO:
                default:
                    return new StandardLcsSolver(); // Default to standard
            }
        }

        /**
         * Creates a configured instance.
         */
        LcsConfig build() {
            return new LcsConfig(this);
        }
    }

    final AlgorithmPreference algorithm;
    final boolean cacheEnabled;
    final boolean normalizationEnabled;
    final NormalizerPreset normalizer;
    final double approximateThreshold;
    final int approximateBandWidth;
    final boolean instrumentationEnabled;
    final int cacheSizeLimit;

    LcsConfig(Builder builder) {
        this.algorithm = builder.algo;
        this.cacheEnabled = builder.enableCache;
        this.normalizationEnabled = builder.enableNormalization;
        this.normalizer = builder.normalizer;
        this.approximateThreshold = builder.approximateSimilarityThreshold;
        this.approximateBandWidth = builder.approximateBandWidthPercent;
        this.instrumentationEnabled = builder.enableInstrumentation;
        this.cacheSizeLimit = builder.cacheSizeLimit;
    }

    /**
     * Creates a default configuration.
     */
    static LcsConfig defaultConfig() {
        return new Builder().build();
    }

    /**
     * Creates a performance-optimized configuration (space-efficient).
     */
    static LcsConfig performance() {
        return new Builder()
                .algorithm(AlgorithmPreference.SPACE_OPTIMIZED)
                .cache(true)
                .build();
    }

    /**
     * Creates a strict comparison configuration (no normalization).
     */
    static LcsConfig strict() {
        return new Builder()
                .algorithm(AlgorithmPreference.STANDARD)
                .normalization(false)
                .build();
    }

    /**
     * Creates a lenient configuration (case-insensitive, whitespace-tolerant).
     */
    static LcsConfig lenient() {
        return new Builder()
                .algorithm(AlgorithmPreference.AUTO)
                .normalization(true)
                .normalizer(NormalizerPreset.CASE_INSENSITIVE)
                .cache(true)
                .build();
    }

    /**
     * Creates a text-matching configuration (remove punctuation, case-insensitive).
     */
    static LcsConfig textMatching() {
        return new Builder()
                .algorithm(AlgorithmPreference.AUTO)
                .normalization(true)
                .normalizer(NormalizerPreset.TEXT_ONLY)
                .cache(true)
                .build();
    }

    /**
     * Creates a configuration optimized for very large inputs.
     */
    static LcsConfig largeInputs() {
        return new Builder()
                .algorithm(AlgorithmPreference.APPROXIMATE)
                .approximateThreshold(0.75)
                .approximateBandWidth(15)
                .cache(true)
                .build();
    }

    @Override
    public String toString() {
        return String.format(
                "LcsConfig{algo=%s, cache=%s, norm=%s, instrumentation=%s}",
                algorithm, cacheEnabled, normalizationEnabled, instrumentationEnabled
        );
    }
}
