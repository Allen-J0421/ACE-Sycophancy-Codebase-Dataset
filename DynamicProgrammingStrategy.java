public class DynamicProgrammingStrategy implements CoinChangeStrategy {

  @Override
  public int countWays(int[] coins, int targetSum) {
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
}
