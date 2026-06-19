package coinchange;

public final class DynamicProgrammingCoinChangeSolver implements CoinChangeSolver {

    @Override
    public int countWays(CoinChangeRequest request) {
        int targetSum = request.targetSum();
        int[] waysBySum = new int[targetSum + 1];
        waysBySum[0] = 1;

        for (int coin : request.denominations().values()) {
            for (int currentSum = coin; currentSum <= targetSum; currentSum++) {
                waysBySum[currentSum] += waysBySum[currentSum - coin];
            }
        }

        return waysBySum[targetSum];
    }
}
