public class CoinChange {

  private static final CoinChangeStrategy DEFAULT_STRATEGY = new DynamicProgrammingStrategy();

  public static CoinChangeResult solve(int[] coins, int targetSum) {
    CoinChangeSolver solver = new CoinChangeSolver(DEFAULT_STRATEGY);
    return solver.solve(coins, targetSum);
  }

  public static void main(String[] args) {
    int[] coins = {1, 2, 3};
    int targetSum = 5;

    try {
      CoinChangeResult result = solve(coins, targetSum);
      System.out.println("Number of ways to make sum " + result.getTargetSum() + ": " + result.getWays());
      System.out.println(result);
    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
