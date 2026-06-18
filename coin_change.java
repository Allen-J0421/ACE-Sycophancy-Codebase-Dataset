final class CoinChange {
    private static final int[] EXAMPLE_COINS = {1, 2, 3};
    private static final int EXAMPLE_AMOUNT = 5;

    private CoinChange() {
    }

    static int count(int[] coins, int targetAmount) {
        return countWays(coins, targetAmount);
    }

    static int countWays(int[] coins, int targetAmount) {
        int[] ways = new int[targetAmount + 1];
        ways[0] = 1;

        for (int coin : coins) {
            for (int currentAmount = coin; currentAmount < ways.length; currentAmount++) {
                ways[currentAmount] += ways[currentAmount - coin];
            }
        }

        return ways[targetAmount];
    }

    public static void main(String[] args) {
        printExampleResult();
    }

    private static void printExampleResult() {
        System.out.println(countWays(EXAMPLE_COINS, EXAMPLE_AMOUNT));
    }
}
