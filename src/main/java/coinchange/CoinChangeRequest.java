package coinchange;

import java.util.Arrays;

public record CoinChangeRequest(int[] coins, int targetSum) {

    public CoinChangeRequest {
        validateCoins(coins);
        validateTargetSum(targetSum);
        coins = Arrays.copyOf(coins, coins.length);
    }

    @Override
    public int[] coins() {
        return Arrays.copyOf(coins, coins.length);
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
