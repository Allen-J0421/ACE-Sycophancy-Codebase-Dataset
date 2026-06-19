/**
 * LCS solver decorator that applies input normalization.
 * Enables clean separation between preprocessing and computation.
 * Works with any base solver implementation.
 */
class NormalizingLcsSolver implements LcsSolver {
    private final LcsSolver baseSolver;
    private final InputNormalizer normalizer;

    /**
     * Creates a normalizing solver wrapping another solver.
     *
     * @param baseSolver the underlying solver
     * @param normalizer the normalization strategy
     */
    NormalizingLcsSolver(LcsSolver baseSolver, InputNormalizer normalizer) {
        this.baseSolver = baseSolver;
        this.normalizer = normalizer;
    }

    @Override
    public LcsResult solve(LcsInput input) {
        // Normalize both input strings
        String normalized1 = normalizer.normalize(input.getFirstString());
        String normalized2 = normalizer.normalize(input.getSecondString());

        // Create new input with normalized strings
        LcsInput normalizedInput = new LcsInput(normalized1, normalized2);

        // Solve with normalized input
        return baseSolver.solve(normalizedInput);
    }

    /**
     * Gets the base solver.
     */
    LcsSolver getBaseSolver() {
        return baseSolver;
    }

    /**
     * Gets the normalizer being used.
     */
    InputNormalizer getNormalizer() {
        return normalizer;
    }

    @Override
    public String toString() {
        return "NormalizingLcsSolver(" + baseSolver.getClass().getSimpleName() + ", " + normalizer + ")";
    }
}

/**
 * Factory methods for creating normalized solvers.
 * Convenience utilities for common normalization patterns.
 */
class NormalizedSolvers {
    /**
     * Case-insensitive solver: uses standard solver with lowercase normalization.
     */
    static LcsSolver caseInsensitive() {
        return new NormalizingLcsSolver(
                LcsSolverFactory.standard(),
                InputNormalizers.caseInsensitive()
        );
    }

    /**
     * Whitespace-insensitive solver: collapses all whitespace.
     */
    static LcsSolver whitespaceInsensitive() {
        return new NormalizingLcsSolver(
                LcsSolverFactory.standard(),
                InputNormalizers.whitespaceInsensitive()
        );
    }

    /**
     * Text-only solver: ignores case, whitespace, and punctuation.
     */
    static LcsSolver textOnly() {
        return new NormalizingLcsSolver(
                LcsSolverFactory.standard(),
                InputNormalizers.textOnly()
        );
    }

    /**
     * Cached + case-insensitive solver.
     * Good for repeated queries with case variations.
     */
    static LcsSolver cachedCaseInsensitive() {
        return new NormalizingLcsSolver(
                LcsSolverFactory.cached(),
                InputNormalizers.caseInsensitive()
        );
    }

    /**
     * Custom normalized solver using a specific normalizer and base solver.
     *
     * @param baseSolver the base LCS solver
     * @param normalizer the normalization strategy
     * @return normalized solver wrapping the base
     */
    static LcsSolver with(LcsSolver baseSolver, InputNormalizer normalizer) {
        return new NormalizingLcsSolver(baseSolver, normalizer);
    }

    /**
     * Builder for creating custom normalized solvers.
     */
    static class Builder {
        private LcsSolver baseSolver = LcsSolverFactory.standard();
        private InputNormalizer normalizer = InputNormalizers.none();

        /**
         * Sets the base solver.
         */
        Builder solver(LcsSolver s) {
            this.baseSolver = s;
            return this;
        }

        /**
         * Uses standard solver (default).
         */
        Builder standardSolver() {
            this.baseSolver = LcsSolverFactory.standard();
            return this;
        }

        /**
         * Uses space-optimized solver.
         */
        Builder spaceOptimizedSolver() {
            this.baseSolver = LcsSolverFactory.spaceOptimized();
            return this;
        }

        /**
         * Uses cached solver.
         */
        Builder cachedSolver() {
            this.baseSolver = LcsSolverFactory.cached();
            return this;
        }

        /**
         * Sets the normalizer.
         */
        Builder normalizer(InputNormalizer n) {
            this.normalizer = n;
            return this;
        }

        /**
         * Uses case-insensitive normalization.
         */
        Builder caseInsensitive() {
            this.normalizer = InputNormalizers.caseInsensitive();
            return this;
        }

        /**
         * Uses whitespace-insensitive normalization.
         */
        Builder whitespaceInsensitive() {
            this.normalizer = InputNormalizers.whitespaceInsensitive();
            return this;
        }

        /**
         * Uses text-only normalization.
         */
        Builder textOnly() {
            this.normalizer = InputNormalizers.textOnly();
            return this;
        }

        /**
         * Uses custom normalization.
         */
        Builder customNormalizer(InputNormalizer n) {
            this.normalizer = n;
            return this;
        }

        /**
         * Builds the normalized solver.
         */
        LcsSolver build() {
            return new NormalizingLcsSolver(baseSolver, normalizer);
        }
    }

    /**
     * Creates a builder for custom configurations.
     */
    static Builder builder() {
        return new Builder();
    }
}
