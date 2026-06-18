import java.util.ArrayList;
import java.util.List;

public class GraphBenchmark {
    private final Graph graph;
    private List<BenchmarkResult> results = new ArrayList<>();

    public GraphBenchmark(Graph graph) {
        this.graph = graph;
    }

    public void benchmarkTraversal(GraphTraversal algorithm, String name, int iterations) {
        Logger.info("Benchmarking " + name + " with " + iterations + " iterations");

        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            algorithm.traverse(graph);
            long duration = System.nanoTime() - start;

            totalTime += duration;
            minTime = Math.min(minTime, duration);
            maxTime = Math.max(maxTime, duration);
        }

        double avgTime = totalTime / (double) iterations;
        BenchmarkResult result = new BenchmarkResult(name, minTime, maxTime, avgTime, iterations);
        results.add(result);

        Logger.debug(name + " benchmark complete: " + result);
    }

    public void printResults() {
        System.out.println("\n=== Benchmark Results ===");
        System.out.printf("%-20s %-15s %-15s %-15s %-10s%n",
            "Algorithm", "Min (μs)", "Max (μs)", "Avg (μs)", "Iterations");
        System.out.println("-".repeat(75));

        for (BenchmarkResult result : results) {
            System.out.printf("%-20s %-15.2f %-15.2f %-15.2f %-10d%n",
                result.name,
                result.minNanos / 1000.0,
                result.maxNanos / 1000.0,
                result.avgNanos / 1000.0,
                result.iterations);
        }
    }

    public List<BenchmarkResult> getResults() {
        return new ArrayList<>(results);
    }

    public static class BenchmarkResult {
        public final String name;
        public final long minNanos;
        public final long maxNanos;
        public final double avgNanos;
        public final int iterations;

        public BenchmarkResult(String name, long minNanos, long maxNanos, double avgNanos, int iterations) {
            this.name = name;
            this.minNanos = minNanos;
            this.maxNanos = maxNanos;
            this.avgNanos = avgNanos;
            this.iterations = iterations;
        }

        @Override
        public String toString() {
            return String.format("%s [min: %.0f ns, max: %.0f ns, avg: %.0f ns]",
                name, (double) minNanos, (double) maxNanos, avgNanos);
        }
    }
}
