/**
 * Factory for creating and composing LCS solver instances.
 * Provides convenient static methods for common solver configurations
 * and eliminates boilerplate solver instantiation patterns.
 */
class LcsSolverFactory {

    /**
     * Creates a standard DP-based LCS solver.
     * Suitable for general-purpose use.
     *
     * Time: O(m * n), Space: O(m * n)
     *
     * @return standard LCS solver
     */
    static LcsSolver standard() {
        return new StandardLcsSolver();
    }

    /**
     * Creates a space-optimized LCS solver.
     * Uses rolling array technique to reduce memory usage.
     * Suitable for memory-constrained environments.
     *
     * Time: O(m * n), Space: O(min(m, n))
     *
     * @return space-optimized LCS solver
     */
    static LcsSolver spaceOptimized() {
        return new SpaceOptimizedLcsSolver();
    }

    /**
     * Creates a cached LCS solver wrapping the standard solver.
     * Caches results for repeated queries on same string pairs.
     * Useful when the same comparisons are performed multiple times.
     *
     * @return cached solver (standard + caching)
     */
    static LcsSolver cached() {
        return new CachedLcsSolver(new StandardLcsSolver());
    }

    /**
     * Creates a cached LCS solver wrapping the space-optimized solver.
     * Combines space optimization with caching for memory-constrained scenarios
     * with repeated queries.
     *
     * @return cached space-optimized solver
     */
    static LcsSolver cachedSpaceOptimized() {
        return new CachedLcsSolver(new SpaceOptimizedLcsSolver());
    }

    /**
     * Creates a cached solver wrapping a custom base solver.
     * Useful for applying caching to custom solver implementations.
     *
     * @param baseSolver the solver to wrap with caching
     * @return cached version of the provided solver
     */
    static LcsSolver withCaching(LcsSolver baseSolver) {
        return new CachedLcsSolver(baseSolver);
    }

    /**
     * Creates a standard solver configured for use with a custom character matcher.
     * Enables case-insensitive, whitespace-insensitive, or custom matching logic.
     *
     * @param matcher custom character comparison strategy
     * @return standard solver with custom matching
     */
    static LcsSolver standardWithMatcher(CharacterMatcher matcher) {
        return new StandardLcsWithMatcher(matcher);
    }

    /**
     * Gets the recommended solver for the given input size.
     * Automatically selects appropriate solver based on string lengths.
     *
     * Strategy:
     * - Small inputs (< 1000): standard solver (simplicity + good performance)
     * - Medium inputs (1000-10000): space-optimized (balanced)
     * - Large inputs (> 10000): space-optimized + caching (if repeated)
     *
     * @param estimatedSize approximate total characters in both strings
     * @return recommended solver
     */
    static LcsSolver recommended(int estimatedSize) {
        if (estimatedSize < 1000) {
            return standard();
        } else if (estimatedSize < 10000) {
            return spaceOptimized();
        } else {
            // For very large inputs, caching helps with related queries
            return cachedSpaceOptimized();
        }
    }

    /**
     * Builder for constructing custom solver configurations.
     * Provides fluent API for composing solvers.
     */
    static class Builder {
        private LcsSolver baseSolver;
        private boolean caching = false;
        private CharacterMatcher matcher = null;

        /**
         * Sets the base solver algorithm.
         *
         * @param type the solver type (STANDARD, SPACE_OPTIMIZED)
         * @return this builder
         */
        Builder withBase(SolverType type) {
            this.baseSolver = switch (type) {
                case STANDARD -> new StandardLcsSolver();
                case SPACE_OPTIMIZED -> new SpaceOptimizedLcsSolver();
            };
            return this;
        }

        /**
         * Enables result caching for the solver.
         *
         * @return this builder
         */
        Builder withCaching() {
            this.caching = true;
            return this;
        }

        /**
         * Sets a custom character matcher for the solver.
         *
         * @param characterMatcher the matching strategy
         * @return this builder
         */
        Builder withMatcher(CharacterMatcher characterMatcher) {
            this.matcher = characterMatcher;
            return this;
        }

        /**
         * Builds the configured solver.
         *
         * @return the composed solver
         */
        LcsSolver build() {
            if (baseSolver == null) {
                baseSolver = new StandardLcsSolver();
            }

            LcsSolver result = baseSolver;

            // Apply matcher if specified
            if (matcher != null && baseSolver instanceof StandardLcsSolver) {
                result = new StandardLcsWithMatcher(matcher);
            }

            // Apply caching last (decorates the entire chain)
            if (caching) {
                result = new CachedLcsSolver(result);
            }

            return result;
        }
    }

    /**
     * Creates a builder for custom solver configuration.
     *
     * @return new builder instance
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Solver type enumeration for factory selection.
     */
    enum SolverType {
        /**
         * Standard DP implementation.
         */
        STANDARD,

        /**
         * Space-optimized variant.
         */
        SPACE_OPTIMIZED
    }
}

/**
 * Standard LCS solver with custom character matcher support.
 * Internally used by factory when matcher is specified.
 */
class StandardLcsWithMatcher implements LcsSolver {
    private final CharacterMatcher matcher;

    StandardLcsWithMatcher(CharacterMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public LcsResult solve(LcsInput input) {
        int[][] dpTable = DpTableBuilder.buildTableWithMatcher(
                input.getFirstString(),
                input.getSecondString(),
                matcher
        );
        return new LcsResult(dpTable[input.getFirstString().length()]
                [input.getSecondString().length()]);
    }
}
