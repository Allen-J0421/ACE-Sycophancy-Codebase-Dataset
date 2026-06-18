public class InputValidator {

  public static void validate(int[] coins, int targetSum) {
    validateCoins(coins);
    validateTargetSum(targetSum);
  }

  private static void validateCoins(int[] coins) {
    if (coins == null || coins.length == 0) {
      throw new InvalidCoinsException("Coins array cannot be null or empty");
    }
    for (int coin : coins) {
      if (coin <= 0) {
        throw new InvalidCoinsException("All coin values must be positive");
      }
    }
  }

  private static void validateTargetSum(int targetSum) {
    if (targetSum < 0) {
      throw new InvalidTargetSumException("Target sum cannot be negative");
    }
  }
}
