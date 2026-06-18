public class CoinChangeDemo {

  public static void main(String[] args) {
    int[] coins = {1, 2, 3};
    int targetSum = 5;

    System.out.println("=== Coin Change Problem Solver ===\n");

    try {
      demonstrateStrategy("Dynamic Programming (2D DP)",
          new DynamicProgrammingStrategy(), coins, targetSum);

      demonstrateStrategy("Space Optimized (1D DP)",
          new SpaceOptimizedStrategy(), coins, targetSum);

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  private static void demonstrateStrategy(String strategyName,
      CoinChangeStrategy strategy, int[] coins, int targetSum) {
    System.out.println("Strategy: " + strategyName);

    CoinChangeSolver solver = new CoinChangeSolver(strategy);
    CoinChangeResult result = solver.solve(coins, targetSum);

    System.out.println("  Coins: " + java.util.Arrays.toString(result.getCoins()));
    System.out.println("  Target Sum: " + result.getTargetSum());
    System.out.println("  Number of Ways: " + result.getWays());
    System.out.println();
  }
}
