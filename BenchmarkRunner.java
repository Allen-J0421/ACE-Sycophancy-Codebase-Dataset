import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {
  private final List<BenchmarkResult> results = new ArrayList<>();

  public BenchmarkResult benchmark(String name, CoinChangeStrategy strategy,
      int[] coins, int targetSum, int iterations) {
    long startTime = System.nanoTime();

    for (int i = 0; i < iterations; i++) {
      strategy.countWays(coins, targetSum);
    }

    long endTime = System.nanoTime();
    long totalTime = endTime - startTime;
    BenchmarkResult result = new BenchmarkResult(name, iterations, totalTime);
    results.add(result);
    return result;
  }

  public void printResults() {
    System.out.println("\n=== Benchmark Results ===");
    for (BenchmarkResult result : results) {
      System.out.println(result);
    }
    if (!results.isEmpty()) {
      printSummary();
    }
  }

  private void printSummary() {
    System.out.println("\n=== Summary ===");
    if (results.size() >= 2) {
      BenchmarkResult fastest = results.get(0);
      for (BenchmarkResult r : results) {
        if (r.getAverageTimeNanos() < fastest.getAverageTimeNanos()) {
          fastest = r;
        }
      }
      System.out.println("Fastest: " + fastest.getName());
      for (BenchmarkResult r : results) {
        if (!r.equals(fastest)) {
          double multiplier = (double) r.getTotalTimeNanos() / fastest.getTotalTimeNanos();
          System.out.println("  " + r.getName() + " is " + String.format("%.2f", multiplier) + "x slower");
        }
      }
    }
  }

  public static class BenchmarkResult {
    private final String name;
    private final int iterations;
    private final long totalTimeNanos;

    public BenchmarkResult(String name, int iterations, long totalTimeNanos) {
      this.name = name;
      this.iterations = iterations;
      this.totalTimeNanos = totalTimeNanos;
    }

    public String getName() { return name; }
    public int getIterations() { return iterations; }
    public long getTotalTimeNanos() { return totalTimeNanos; }

    public long getAverageTimeNanos() {
      return totalTimeNanos / iterations;
    }

    @Override
    public String toString() {
      return String.format("%s: %d iterations, %.2fms total, %.4fμs per call",
          name, iterations, totalTimeNanos / 1_000_000.0, getAverageTimeNanos() / 1000.0);
    }
  }
}
