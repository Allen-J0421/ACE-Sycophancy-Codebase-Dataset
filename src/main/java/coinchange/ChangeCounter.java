package coinchange;

final class ChangeCounter {

    private ChangeCounter() {
    }

    static int countWays(CoinChangeRequest request) {
        int[] coins = request.denominations().rawValues();
        int targetSum = request.targetSum();
        int[] waysBySum = new int[targetSum + 1];
        waysBySum[0] = 1;

        for (int coin : coins) {
            for (int currentSum = coin; currentSum <= targetSum; currentSum++) {
                waysBySum[currentSum] += waysBySum[currentSum - coin];
            }
        }

        return waysBySum[targetSum];
    }
}
