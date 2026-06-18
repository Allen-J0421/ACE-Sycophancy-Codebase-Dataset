public class CoinChange {

  static int countWays(int[] coins, int targetSum) {
    validateInput(coins, targetSum);
    return countCoinsDP(coins, targetSum);
  }

  private static void validateInput(int[] coins, int targetSum) {
    if (coins == null || coins.length == 0) {
      throw new IllegalArgumentException("Coins array cannot be null or empty");
    }
    if (targetSum < 0) {
      throw new IllegalArgumentException("Target sum cannot be negative");
    }
  }

  private static int countCoinsDP(int[] coins, int targetSum) {
    int numCoins = coins.length;
    int[][] dp = new int[numCoins + 1][targetSum + 1];

    dp[0][0] = 1;

    for (int i = 1; i <= numCoins; i++) {
      for (int j = 0; j <= targetSum; j++) {
        dp[i][j] += dp[i - 1][j];

        int coinValue = coins[i - 1];
        if (j >= coinValue) {
          dp[i][j] += dp[i][j - coinValue];
        }
      }
    }

    return dp[numCoins][targetSum];
  }

  public static void main(String[] args) {
    int[] coins = {1, 2, 3};
    int targetSum = 5;

    try {
      int result = countWays(coins, targetSum);
      System.out.println("Number of ways to make sum " + targetSum + ": " + result);
    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
