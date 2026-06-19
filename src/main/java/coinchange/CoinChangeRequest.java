package coinchange;

public record CoinChangeRequest(CoinDenominations denominations, int targetSum) {

    public CoinChangeRequest(int[] coins, int targetSum) {
        this(new CoinDenominations(coins), targetSum);
    }

    public CoinChangeRequest {
        if (denominations == null) {
            throw new IllegalArgumentException("denominations must not be null");
        }
        validateTargetSum(targetSum);
    }

    private static void validateTargetSum(int targetSum) {
        if (targetSum < 0) {
            throw new IllegalArgumentException("targetSum must not be negative");
        }
    }
}
