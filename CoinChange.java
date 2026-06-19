public final class CoinChange {

    private CoinChange() {
    }

    static int count(int[] coins, int targetSum) {
        int[] waysBySum = new int[targetSum + 1];
        waysBySum[0] = 1;

        for (int coin : coins) {
            for (int currentSum = coin; currentSum <= targetSum; currentSum++) {
                waysBySum[currentSum] += waysBySum[currentSum - coin];
            }
        }

        return waysBySum[targetSum];
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int targetSum = 5;
        System.out.println(count(coins, targetSum));
    }
}
