/**
 * Adaptive LCS solver that selects algorithm based on input characteristics.
 * Analyzes string sizes, similarity, and character distribution to choose optimal solver.
 */
class AdaptiveSolver implements LcsSolver {

    /**
     * Input profile characteristics.
     */
    static class InputProfile {
        final int len1;
        final int len2;
        final int maxLen;
        final double lenRatio;
        final int commonChars;
        final double charDiversity;

        InputProfile(String s1, String s2) {
            this.len1 = s1.length();
            this.len2 = s2.length();
            this.maxLen = Math.max(len1, len2);
            this.lenRatio = Math.min(len1, len2) / (double) Math.max(len1, len2);
            this.commonChars = countCommonCharacters(s1, s2);
            this.charDiversity = calculateCharDiversity(s1, s2);
        }

        /**
         * Count distinct characters that appear in both strings.
         */
        private int countCommonCharacters(String s1, String s2) {
            java.util.Set<Character> chars1 = new java.util.HashSet<>();
            for (char c : s1.toCharArray()) {
                chars1.add(c);
            }

            int count = 0;
            for (char c : s2.toCharArray()) {
                if (chars1.contains(c)) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Calculate character diversity (entropy) of combined input.
         */
        private double calculateCharDiversity(String s1, String s2) {
            java.util.Map<Character, Integer> freq = new java.util.HashMap<>();
            for (char c : (s1 + s2).toCharArray()) {
                freq.put(c, freq.getOrDefault(c, 0) + 1);
            }

            if (freq.isEmpty()) return 0;

            int total = s1.length() + s2.length();
            double entropy = 0;
            for (int count : freq.values()) {
                double p = count / (double) total;
                entropy -= p * Math.log(p) / Math.log(2);
            }
            return entropy;
        }

        @Override
        public String toString() {
            return String.format("Profile(size=%dx%d, ratio=%.2f, common=%d, diversity=%.2f)",
                    len1, len2, lenRatio, commonChars, charDiversity);
        }
    }

    /**
     * Recommendation for best solver based on input.
     */
    enum SolverRecommendation {
        STANDARD("Standard DP: balanced complexity"),
        SPACE_OPTIMIZED("Space-optimized: memory-constrained"),
        APPROXIMATE("Approximate: very large inputs"),
        SUBSTRING("Substring: highly asymmetric"),
        CACHED("Cached: frequent queries");

        final String description;

        SolverRecommendation(String description) {
            this.description = description;
        }
    }

    private final LcsSolver fallbackSolver;

    /**
     * Creates adaptive solver with fallback.
     */
    AdaptiveSolver(LcsSolver fallbackSolver) {
        this.fallbackSolver = fallbackSolver;
    }

    /**
     * Creates default adaptive solver with standard fallback.
     */
    AdaptiveSolver() {
        this(new StandardLcsSolver());
    }

    @Override
    public LcsResult solve(LcsInput input) {
        InputProfile profile = new InputProfile(input.getFirstString(), input.getSecondString());
        SolverRecommendation recommendation = selectSolver(profile);
        LcsSolver chosen = createSolver(recommendation);
        return chosen.solve(input);
    }

    /**
     * Analyzes input and selects best solver.
     */
    SolverRecommendation selectSolver(InputProfile profile) {
        // Very large inputs: use approximation
        if (profile.maxLen > 5000) {
            return SolverRecommendation.APPROXIMATE;
        }

        // Highly asymmetric (pattern matching scenario)
        if (profile.lenRatio < 0.1) {
            return SolverRecommendation.SUBSTRING;
        }

        // Small inputs: standard DP is fine
        if (profile.maxLen < 500) {
            return SolverRecommendation.STANDARD;
        }

        // Medium inputs with low character diversity (repetitive)
        if (profile.charDiversity < 2.0) {
            return SolverRecommendation.SPACE_OPTIMIZED;
        }

        // Medium inputs: standard is good default
        return SolverRecommendation.STANDARD;
    }

    private LcsSolver createSolver(SolverRecommendation recommendation) {
        switch (recommendation) {
            case STANDARD:
                return new StandardLcsSolver();
            case SPACE_OPTIMIZED:
                return new SpaceOptimizedLcsSolver();
            case APPROXIMATE:
                return new ApproximateLcsSolver();
            case SUBSTRING:
                return new SubstringLcsSolver();
            case CACHED:
                return LcsSolverFactory.cached();
            default:
                return fallbackSolver;
        }
    }

    @Override
    public String toString() {
        return "AdaptiveSolver(selects best algorithm per input)";
    }
}

/**
 * Self-optimizing solver that learns from execution patterns.
 * Tracks which algorithms perform best for different input characteristics.
 */
class SelfOptimizingSolver implements LcsSolver {

    static class SolverChoice {
        final LcsSolver solver;
        int successes = 0;
        long totalNanos = 0;

        SolverChoice(LcsSolver solver) {
            this.solver = solver;
        }

        double avgNanos() {
            return successes > 0 ? (double) totalNanos / successes : 0;
        }

        void recordExecution(long nanos) {
            successes++;
            totalNanos += nanos;
        }
    }

    private final java.util.Map<String, SolverChoice> solverStats = new java.util.HashMap<>();
    private final LcsSolver[] candidates;

    /**
     * Creates self-optimizing solver with candidate solvers.
     */
    SelfOptimizingSolver(LcsSolver... candidates) {
        this.candidates = candidates;
        for (LcsSolver solver : candidates) {
            solverStats.put(solver.toString(), new SolverChoice(solver));
        }
    }

    @Override
    public LcsResult solve(LcsInput input) {
        if (candidates.length == 0) {
            throw new IllegalStateException("No candidate solvers");
        }

        // Select best performing solver based on history
        LcsSolver best = selectBestSolver();

        long startNano = System.nanoTime();
        LcsResult result = best.solve(input);
        long elapsedNano = System.nanoTime() - startNano;

        // Record performance
        String solverName = best.toString();
        SolverChoice choice = solverStats.get(solverName);
        if (choice != null) {
            choice.recordExecution(elapsedNano);
        }

        return result;
    }

    private LcsSolver selectBestSolver() {
        // Warm-up phase: try each solver once
        long totalExecutions = solverStats.values().stream()
                .mapToLong(sc -> sc.successes)
                .sum();

        if (totalExecutions < candidates.length) {
            // Exploration: return next untried solver
            return candidates[(int) totalExecutions % candidates.length];
        }

        // Exploitation: return fastest solver by average time
        return solverStats.values().stream()
                .min(java.util.Comparator.comparingDouble(SolverChoice::avgNanos))
                .map(sc -> sc.solver)
                .orElse(candidates[0]);
    }

    /**
     * Gets performance statistics.
     */
    java.util.Map<String, Long> getStats() {
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        solverStats.forEach((name, choice) -> {
            stats.put(name, Math.round(choice.avgNanos()));
        });
        return stats;
    }

    @Override
    public String toString() {
        return String.format("SelfOptimizingSolver(%d candidates)", candidates.length);
    }
}
