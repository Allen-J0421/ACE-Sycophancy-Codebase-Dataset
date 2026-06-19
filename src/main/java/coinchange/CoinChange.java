package coinchange;

public final class CoinChange {

    private CoinChange() {
    }

    public static int count(int[] coins, int targetSum) {
        return count(new CoinChangeRequest(coins, targetSum));
    }

    public static int count(CoinDenominations denominations, int targetSum) {
        return count(new CoinChangeRequest(denominations, targetSum));
    }

    public static int count(CoinChangeRequest request) {
        return ChangeCounter.countWays(request);
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int targetSum = 5;
        System.out.println(count(coins, targetSum));
    }
}
