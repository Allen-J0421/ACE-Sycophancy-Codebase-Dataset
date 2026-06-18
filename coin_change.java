class CoinChange {
    private static final int[] EXAMPLE_COINS = {1, 2, 3};
    private static final int EXAMPLE_AMOUNT = 5;

    static int count(int[] coins, int amount) {
        return countWays(coins, amount);
    }

    static int countWays(int[] coins, int amount) {
        int[] ways = new int[amount + 1];
        ways[0] = 1;

        for (int coin : coins) {
            for (int currentAmount = coin; currentAmount <= amount; currentAmount++) {
                ways[currentAmount] += ways[currentAmount - coin];
            }
        }

        return ways[amount];
    }

    public static void main(String[] args) {
        printExampleResult();
    }

    private static void printExampleResult() {
        System.out.println(countWays(EXAMPLE_COINS, EXAMPLE_AMOUNT));
    }
}
