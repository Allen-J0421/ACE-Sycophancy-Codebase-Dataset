public final class CoinChange {

    private CoinChange() {
    }

    static int count(int[] coins, int targetSum) {
        return ChangeCounter.countWays(new CoinChangeRequest(coins, targetSum));
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int targetSum = 5;
        System.out.println(count(coins, targetSum));
    }
}
