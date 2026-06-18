package sorting.analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class ComparativeBenchmark<T extends Comparable<T>> {
    private final List<BenchmarkCandidate<T>> candidates;

    public ComparativeBenchmark() {
        this.candidates = new ArrayList<>();
    }

    public ComparativeBenchmark<T> addAlgorithm(
            String name,
            BiFunction<T[], Comparator<T>, Long> sortFunction) {
        candidates.add(new BenchmarkCandidate<>(name, sortFunction));
        return this;
    }

    public BenchmarkResults run(T[] data, int iterations) {
        List<BenchmarkResults.AlgorithmResult> results = new ArrayList<>();

        for (BenchmarkCandidate<T> candidate : candidates) {
            long totalDuration = 0;

            for (int i = 0; i < iterations; i++) {
                @SuppressWarnings("unchecked")
                T[] copy = (T[]) data.clone();
                long duration = candidate.sortFunction.apply(copy, Comparable::compareTo);
                totalDuration += duration;
            }

            long avgDuration = totalDuration / iterations;
            results.add(new BenchmarkResults.AlgorithmResult(candidate.name, avgDuration));
        }

        results.sort(Comparator.comparingLong(BenchmarkResults.AlgorithmResult::avgDurationNanos));
        return new BenchmarkResults(results);
    }

    public static class BenchmarkResults {
        private final List<AlgorithmResult> results;

        public BenchmarkResults(List<AlgorithmResult> results) {
            this.results = results;
        }

        public AlgorithmResult fastest() {
            return results.isEmpty() ? null : results.get(0);
        }

        public String summary() {
            if (results.isEmpty()) return "No results";

            StringBuilder sb = new StringBuilder("Benchmark Results:\n");
            AlgorithmResult fastest = fastest();

            for (AlgorithmResult result : results) {
                double ratio = (double) result.avgDurationNanos() / fastest.avgDurationNanos();
                String bar = "█".repeat((int) Math.min(ratio * 10, 50));
                sb.append(String.format("  %-20s: %8.3f ms [%s] %.2fx\n",
                        result.name(),
                        result.avgDurationNanos() / 1_000_000.0,
                        bar,
                        ratio));
            }

            return sb.toString();
        }

        public record AlgorithmResult(String name, long avgDurationNanos) {
        }
    }

    private record BenchmarkCandidate<T>(
            String name,
            BiFunction<T[], Comparator<T>, Long> sortFunction) {
    }
}
