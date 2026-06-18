public class CoinChangeTest {
  private static int testsPassed = 0;
  private static int testsFailed = 0;

  public static void main(String[] args) {
    System.out.println("=== Coin Change Test Suite ===\n");

    testBasicFunctionality();
    testEdgeCases();
    testValidation();
    testMultipleStrategies();
    testBuilder();
    testMetrics();
    testCaching();

    System.out.println("\n=== Test Summary ===");
    System.out.println("Passed: " + testsPassed);
    System.out.println("Failed: " + testsFailed);
    System.out.println("Total: " + (testsPassed + testsFailed));

    if (testsFailed > 0) {
      System.exit(1);
    }
  }

  private static void testBasicFunctionality() {
    System.out.println("Testing Basic Functionality:");
    test("Basic: coins {1,2,3}, sum 5 equals 5",
        () -> CoinChange.solve(new int[]{1, 2, 3}, 5).getWays() == 5);
    test("Basic: coins {1,2}, sum 3 equals 2",
        () -> CoinChange.solve(new int[]{1, 2}, 3).getWays() == 2);
    test("Basic: coins {2}, sum 4 equals 1",
        () -> CoinChange.solve(new int[]{2}, 4).getWays() == 1);
  }

  private static void testEdgeCases() {
    System.out.println("\nTesting Edge Cases:");
    test("Edge: sum 0 equals 1",
        () -> CoinChange.solve(new int[]{1, 2, 3}, 0).getWays() == 1);
    test("Edge: single coin",
        () -> CoinChange.solve(new int[]{5}, 10).getWays() == 1);
    test("Edge: impossible sum returns 0",
        () -> CoinChange.solve(new int[]{2, 5}, 3).getWays() == 0);
  }

  private static void testValidation() {
    System.out.println("\nTesting Validation:");
    test("Validation: null coins throws exception",
        () -> {
          try {
            CoinChange.solve(null, 5);
            return false;
          } catch (InvalidCoinsException e) {
            return true;
          }
        });
    test("Validation: empty coins throws exception",
        () -> {
          try {
            CoinChange.solve(new int[]{}, 5);
            return false;
          } catch (InvalidCoinsException e) {
            return true;
          }
        });
    test("Validation: negative sum throws exception",
        () -> {
          try {
            CoinChange.solve(new int[]{1, 2}, -5);
            return false;
          } catch (InvalidTargetSumException e) {
            return true;
          }
        });
    test("Validation: non-positive coins throw exception",
        () -> {
          try {
            CoinChange.solve(new int[]{1, 0}, 5);
            return false;
          } catch (InvalidCoinsException e) {
            return true;
          }
        });
  }

  private static void testMultipleStrategies() {
    System.out.println("\nTesting Multiple Strategies:");
    int[] coins = {1, 2, 3};
    int targetSum = 5;
    int expected = 5;

    CoinChangeStrategy standard = new DynamicProgrammingStrategy();
    CoinChangeStrategy optimized = new SpaceOptimizedStrategy();

    int resultStandard = standard.countWays(coins, targetSum);
    int resultOptimized = optimized.countWays(coins, targetSum);

    test("Strategy: Standard equals " + expected,
        () -> resultStandard == expected);
    test("Strategy: Space Optimized equals " + expected,
        () -> resultOptimized == expected);
    test("Strategy: Both produce same result",
        () -> resultStandard == resultOptimized);
  }

  private static void testBuilder() {
    System.out.println("\nTesting Builder Pattern:");
    test("Builder: Default strategy",
        () -> CoinChange.builder().build().solve(new int[]{1, 2, 3}, 5).getWays() == 5);
    test("Builder: With specific strategy type",
        () -> CoinChange.builder()
            .withStrategyType(StrategyType.SPACE_OPTIMIZED)
            .build()
            .solve(new int[]{1, 2, 3}, 5).getWays() == 5);
    test("Builder: With caching",
        () -> CoinChange.builder().withCaching().build()
            .solve(new int[]{1, 2, 3}, 5).getWays() == 5);
  }

  private static void testMetrics() {
    System.out.println("\nTesting Metrics:");
    test("Metrics: Collected when enabled",
        () -> CoinChange.builder().enableMetrics().build()
            .solve(new int[]{1, 2, 3}, 5).hasMetrics());
    test("Metrics: Not collected by default",
        () -> !CoinChange.solve(new int[]{1, 2, 3}, 5).hasMetrics());
    test("Metrics: Contains execution time",
        () -> {
          CoinChangeResult result = CoinChange.builder().enableMetrics().build()
              .solve(new int[]{1, 2, 3}, 5);
          return result.getMetrics().getExecutionTimeNanos() >= 0;
        });
  }

  private static void testCaching() {
    System.out.println("\nTesting Caching:");
    test("Caching: CachedStrategy wraps delegate",
        () -> {
          CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
          return cached.countWays(new int[]{1, 2, 3}, 5) == 5;
        });
    test("Caching: Cache grows with calls",
        () -> {
          CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
          cached.countWays(new int[]{1, 2, 3}, 5);
          cached.countWays(new int[]{1, 2}, 3);
          return cached.getCacheSize() == 2;
        });
    test("Caching: Same input returns cached result",
        () -> {
          CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
          int first = cached.countWays(new int[]{1, 2, 3}, 5);
          int second = cached.countWays(new int[]{1, 2, 3}, 5);
          return first == second && cached.getCacheSize() == 1;
        });
  }

  private static void test(String name, TestCase testCase) {
    try {
      if (testCase.run()) {
        System.out.println("  ✓ " + name);
        testsPassed++;
      } else {
        System.out.println("  ✗ " + name);
        testsFailed++;
      }
    } catch (Exception e) {
      System.out.println("  ✗ " + name + " (Exception: " + e.getMessage() + ")");
      testsFailed++;
    }
  }

  interface TestCase {
    boolean run() throws Exception;
  }
}
