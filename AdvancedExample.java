public class AdvancedExample {

  public static void main(String[] args) {
    System.out.println("=== Advanced Coin Change Examples ===\n");

    example1_BasicUsage();
    example2_BuilderPattern();
    example3_PerformanceComparison();
    example4_CachingBenefits();
    example5_ErrorHandling();
  }

  private static void example1_BasicUsage() {
    System.out.println("Example 1: Basic Usage");
    System.out.println("Problem: How many ways to make sum=10 with coins {1,5,2,2}?");

    CoinChangeResult result = CoinChange.solve(new int[]{1, 5, 2, 2}, 10);
    System.out.println("Answer: " + result.getWays() + " ways\n");
  }

  private static void example2_BuilderPattern() {
    System.out.println("Example 2: Builder Pattern with Flexible Configuration");
    int[] coins = {1, 5, 10, 25};
    int targetSum = 50;

    System.out.println("Problem: Make sum=" + targetSum + " with US coins " + java.util.Arrays.toString(coins));

    CoinChangeResult result = CoinChange.builder()
        .withStrategyType(StrategyType.SPACE_OPTIMIZED)
        .enableMetrics()
        .build()
        .solve(coins, targetSum);

    System.out.println("Answer: " + result.getWays() + " ways");
    if (result.hasMetrics()) {
      System.out.println("Performance: " + result.getMetrics() + "\n");
    }
  }

  private static void example3_PerformanceComparison() {
    System.out.println("Example 3: Performance Comparison of Strategies");
    int[] coins = {1, 2, 5, 10};
    int targetSum = 100;

    compareStrategies("Standard (2D DP)", StrategyType.STANDARD, coins, targetSum);
    compareStrategies("Space Optimized (1D DP)", StrategyType.SPACE_OPTIMIZED, coins, targetSum);
    System.out.println();
  }

  private static void compareStrategies(String name, StrategyType type, int[] coins, int targetSum) {
    CoinChangeResult result = CoinChange.builder()
        .withStrategyType(type)
        .enableMetrics()
        .build()
        .solve(coins, targetSum);

    System.out.println("  " + name);
    System.out.println("    Ways: " + result.getWays());
    if (result.hasMetrics()) {
      System.out.println("    " + result.getMetrics());
    }
  }

  private static void example4_CachingBenefits() {
    System.out.println("Example 4: Caching Benefits on Repeated Queries");
    int[] coins = {1, 5, 10, 25};

    CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
    CoinChangeSolver solver = new CoinChangeSolver(cached, true);

    System.out.println("Query 1: sum=25");
    long start1 = System.nanoTime();
    CoinChangeResult r1 = solver.solve(coins, 25);
    long time1 = System.nanoTime() - start1;
    System.out.println("  Result: " + r1.getWays() + " ways, Time: " + (time1 / 1_000_000.0) + "ms");

    System.out.println("Query 2: sum=50");
    long start2 = System.nanoTime();
    CoinChangeResult r2 = solver.solve(coins, 50);
    long time2 = System.nanoTime() - start2;
    System.out.println("  Result: " + r2.getWays() + " ways, Time: " + (time2 / 1_000_000.0) + "ms");

    System.out.println("Query 3: sum=25 (cached)");
    long start3 = System.nanoTime();
    CoinChangeResult r3 = solver.solve(coins, 25);
    long time3 = System.nanoTime() - start3;
    System.out.println("  Result: " + r3.getWays() + " ways, Time: " + (time3 / 1_000_000.0) + "ms");

    System.out.println("Cache Size: " + cached.getCacheSize() + " entries");
    System.out.println("Cache Hit Speedup: " + (double) time1 / time3 + "x faster\n");
  }

  private static void example5_ErrorHandling() {
    System.out.println("Example 5: Comprehensive Error Handling");

    testErrorCase("Null coins", () -> CoinChange.solve(null, 10));
    testErrorCase("Empty coins", () -> CoinChange.solve(new int[]{}, 10));
    testErrorCase("Negative sum", () -> CoinChange.solve(new int[]{1, 5}, -10));
    testErrorCase("Non-positive coin", () -> CoinChange.solve(new int[]{1, 0, 5}, 10));

    System.out.println();
  }

  private static void testErrorCase(String description, ErrorTest test) {
    try {
      test.run();
      System.out.println("  ✗ " + description + " (should have thrown)");
    } catch (InvalidCoinsException e) {
      System.out.println("  ✓ " + description + ": " + e.getClass().getSimpleName());
    } catch (InvalidTargetSumException e) {
      System.out.println("  ✓ " + description + ": " + e.getClass().getSimpleName());
    } catch (Exception e) {
      System.out.println("  ? " + description + ": " + e.getClass().getSimpleName());
    }
  }

  interface ErrorTest {
    void run();
  }
}
