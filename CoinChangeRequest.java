import java.util.Arrays;

final class CoinChangeRequest {
    private final int[] coins;
    private final int targetSum;

    CoinChangeRequest(int[] coins, int targetSum) {
        validateCoins(coins);
        validateTargetSum(targetSum);
        this.coins = Arrays.copyOf(coins, coins.length);
        this.targetSum = targetSum;
    }

    int[] coins() {
        return Arrays.copyOf(coins, coins.length);
    }

    int targetSum() {
        return targetSum;
    }

    private static void validateCoins(int[] coins) {
        if (coins == null) {
            throw new IllegalArgumentException("coins must not be null");
        }

        for (int coin : coins) {
            if (coin <= 0) {
                throw new IllegalArgumentException("coin values must be positive");
            }
        }
    }

    private static void validateTargetSum(int targetSum) {
        if (targetSum < 0) {
            throw new IllegalArgumentException("targetSum must not be negative");
        }
    }
}
