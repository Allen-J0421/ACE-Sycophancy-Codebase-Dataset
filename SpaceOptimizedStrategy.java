public class SpaceOptimizedStrategy implements CoinChangeStrategy {

  @Override
  public int countWays(int[] coins, int targetSum) {
    int[] dp = new int[targetSum + 1];
    dp[0] = 1;

    for (int coin : coins) {
      for (int amount = coin; amount <= targetSum; amount++) {
        dp[amount] += dp[amount - coin];
      }
    }

    return dp[targetSum];
  }
}
