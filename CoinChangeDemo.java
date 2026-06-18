public class CoinChangeDemo {

  public static void main(String[] args) {
    System.out.println("=== Coin Change Problem Solver ===\n");

    demonstrateBasicUsage();
    demonstrateBuilder();
    demonstrateMetrics();
    demonstrateCaching();
  }

  private static void demonstrateBasicUsage() {
    System.out.println("--- Basic Usage ---");
    int[] coins = {1, 2, 3};
    int targetSum = 5;

    CoinChangeResult result = CoinChange.solve(coins, targetSum);
    System.out.println("Coins: " + java.util.Arrays.toString(result.getCoins()));
    System.out.println("Target Sum: " + result.getTargetSum());
    System.out.println("Number of Ways: " + result.getWays());
    System.out.println();
  }

  private static void demonstrateBuilder() {
    System.out.println("--- Builder Pattern (Multiple Strategies) ---");
    int[] coins = {1, 2, 3};
    int targetSum = 5;

    demonstrateStrategyBuilding("Standard 2D DP", StrategyType.STANDARD, coins, targetSum);
    demonstrateStrategyBuilding("Space Optimized 1D DP", StrategyType.SPACE_OPTIMIZED, coins, targetSum);
    System.out.println();
  }

  private static void demonstrateStrategyBuilding(String name, StrategyType type,
      int[] coins, int targetSum) {
    CoinChangeResult result = CoinChange.builder()
        .withStrategyType(type)
        .build()
        .solve(coins, targetSum);

    System.out.println("  " + name + ": " + result.getWays() + " ways");
  }

  private static void demonstrateMetrics() {
    System.out.println("--- Performance Metrics ---");
    int[] coins = {1, 2, 3, 5, 7, 10};
    int targetSum = 50;

    CoinChangeResult result = CoinChange.builder()
        .enableMetrics()
        .build()
        .solve(coins, targetSum);

    System.out.println("Coins: " + java.util.Arrays.toString(result.getCoins()));
    System.out.println("Target Sum: " + result.getTargetSum());
    System.out.println("Number of Ways: " + result.getWays());
    if (result.hasMetrics()) {
      System.out.println("Metrics: " + result.getMetrics());
    }
    System.out.println();
  }

  private static void demonstrateCaching() {
    System.out.println("--- Caching Strategy ---");
    int[] coins = {1, 2, 3};

    CachedStrategy cached = new CachedStrategy(new DynamicProgrammingStrategy());
    CoinChangeSolver solver = new CoinChangeSolver(cached);

    System.out.println("First call (sum=5): " + solver.solve(coins, 5).getWays());
    System.out.println("First call (sum=7): " + solver.solve(coins, 7).getWays());
    System.out.println("Second call (sum=5, from cache): " + solver.solve(coins, 5).getWays());

    System.out.println("Cache size: " + cached.getCacheSize() + " entries");
    System.out.println();
  }
}
