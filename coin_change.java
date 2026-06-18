class CoinChange {

    static int count(int[] coins, int sum) {
        int[] ways = new int[sum + 1];
        ways[0] = 1;

        for (int coin : coins) {
            for (int currentSum = coin; currentSum <= sum; currentSum++) {
                ways[currentSum] += ways[currentSum - coin];
            }
        }

        return ways[sum];
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int sum = 5;
        System.out.println(count(coins, sum));
    }
}
